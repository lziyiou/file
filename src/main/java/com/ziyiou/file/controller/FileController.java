package com.ziyiou.file.controller;

import com.ziyiou.file.mapper.MyFileMapper;
import com.ziyiou.file.mapper.UserMapper;
import com.ziyiou.file.pojo.MyFile;
import com.ziyiou.file.pojo.User;
import com.ziyiou.file.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
public class FileController {

    public String fileRoot = "D:/videos/file";

    @Autowired
    UserService userService;

    @Autowired
    MyFileMapper myFileMapper;

    @Autowired
    UserMapper userMapper;

//    @GetMapping("/test")
//    public String sss(Principal principal, Model model) {
//        model.addAttribute("list", FileUtil.getFiles(
//                new ArrayList<MyFile>(),
//                principal.getName().equals("admin") ?
//                        new File(fileRoot) : new File(fileRoot + "/" + principal.getName())));
//
//        System.err.println(model.getAttribute("list"));
//        return "index.html";
//    }

    @GetMapping("/download")
    public String download(String filename,
                           String folder,
                           Integer fileId,
                           HttpServletResponse res) throws IOException {

        File dest = new File(fileRoot + '/' + folder + '/' + filename);

        // 附件下载
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));

        // 获取目标文件流
        FileInputStream in = new FileInputStream(dest);
        ServletOutputStream out = res.getOutputStream();

        // 更新数据库
        myFileMapper.update(fileId);

        Integer id = userMapper.getIdByUsername(folder);

        // 进行下载
        try {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            System.err.println("下载异常");
        }

        // 关闭资源
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);

        return "redirect:/files"+id;
    }

    @GetMapping("/delete")
    public String delete(String filename,
                         String folder,
                         Integer fileId) {
        File dest = new File(fileRoot + File.separator + folder + File.separator + filename);

        boolean isDeleted = dest.delete();
        if (isDeleted) {
            // 更新数据库
            myFileMapper.delete(fileId);
        }

        Integer userId = userMapper.getIdByUsername(folder);

        return "redirect:/files/" + userId;
    }

    /**
     * 上传文件到服务器
     *
     * @param files 客户端上传的文件
     * @return 反回上传结果
     * @throws IOException 上传文件需要处理的异常
     */
    @PostMapping("/file")
    @ResponseBody
    public User upload(@RequestParam MultipartFile[] files,
                         Integer userId) throws IOException {
        // todo 只有管理员才可以上传文件

        // 获取用户pojo
        User user = userMapper.getUserById(userId);

        // 保存每个文件到硬盘
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new RuntimeException("空文件");
            }
            // 设置文件名
            String oldFileName = "" + file.getOriginalFilename(); // 原文件名，取后缀
            // 去重随机文件名
            String newFileName = UUID.randomUUID().toString() + oldFileName.substring(oldFileName.lastIndexOf(".")); // 新文件名

            // 文件保存的目录
            String path = fileRoot + File.separator + user.getUsername();
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 添加到数据库
            MyFile myFile = new MyFile();
            // 文件所属用户的id
            myFile.setUserId(user.getId());
            myFile.setUploadTime(new Date());
            myFile.setSize(file.getSize());
            myFile.setName(newFileName);
            myFile.setPath(path);
            myFileMapper.insert(myFile);

            file.transferTo(new File(folder, newFileName));

        }
        return new User();
    }

    @GetMapping("/files/{userId}")
    public String getUserFiles(Model model, @PathVariable Integer userId) {
        // 获取要查看的用户
        User user = userMapper.getUserById(userId);

        // 设置权限
        // 管理员可以访问
        // 登录用户的用户名和路径名相同可以访问
        Subject subject = SecurityUtils.getSubject();
        if (!(subject.hasRole("root") || ((User) subject.getPrincipal()).getId().equals(user.getId()))) {
            model.addAttribute("msg", "无权限");
            return "login";
        }

        // 当前用户的所有文件
//        File[] files = new File(fileRoot + File.separator + user.getUsername()).listFiles();
        List<MyFile> files = myFileMapper.getAllMyFilesByUserId(user.getId());
        // 文件按上传时间递减排序
//        if (files != null) {
//            Arrays.sort(files, (f1, f2) -> {
//                long diff = f1.lastModified() - f2.lastModified();
//                if (diff > 0)
//                    return -1;
//                else if (diff == 0)
//                    return 0;
//                else
//                    return 1;
//            });
//        }
        if (!files.isEmpty()) {
            files.sort((f1, f2) -> f1.getUploadTime().before(f2.getUploadTime()) ? 1 : (f1.getUploadTime().equals(f2.getUploadTime()) ? 0 : -1));
        }

        model.addAttribute("files", files);
        // 当前用户
        model.addAttribute("current", (User) subject.getPrincipal());
        // 目录
        model.addAttribute("folder", user.getUsername());

        return "files";
    }

    @GetMapping("/")
    public String getAllFiles(Model model) {
        // 只有管理员可以访问
        Subject subject = SecurityUtils.getSubject();
        if (!subject.hasRole("root")) {
            model.addAttribute("msg", "无权限");
            return "login";
        }

        // 可管理的用户目录，可上传的用户
        model.addAttribute("users", userService.getAllUsers());

        // 当前用户
        model.addAttribute("current", (User) subject.getPrincipal());

        return "index";
    }

    @GetMapping("/users")
    @ResponseBody
    public Set<User> getAllUsers() {
        // 只有管理员可以访问
        Subject subject = SecurityUtils.getSubject();

        // 可管理的用户目录，可上传的用户

        return userService.getAllUsers();
    }

//    // 视频播放
//    @RequestMapping(value = "/preview2", method = RequestMethod.GET)
//    @ResponseBody
//    public void getPreview2(HttpServletResponse response) {
//        try {
//            File file = new File("D:/a.mp4");
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName().replace(" ", "_"));
//            InputStream iStream = new FileInputStream(file);
//            IOUtils.copy(iStream, response.getOutputStream());
//            response.flushBuffer();
//        } catch (java.nio.file.NoSuchFileException e) {
//            response.setStatus(HttpStatus.NOT_FOUND.value());
//        } catch (Exception e) {
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//    }

}