package com.example.campus_trade.controller;

import com.example.campus_trade.entity.Product;
import com.example.campus_trade.service.AiCheckService;
import com.example.campus_trade.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductController implements WebMvcConfigurer {

    private final ProductService productService;
    private final AiCheckService aiCheckService;

    @Value("${upload.folder}")
    private String uploadFolder;

    @Value("${upload.base-url}")
    private String uploadBaseUrl;

    public ProductController(ProductService productService, AiCheckService aiCheckService) {
        this.productService = productService;
        this.aiCheckService = aiCheckService;
    }

    // 图片上传接口
    @PostMapping("/upload")
    public UploadResult upload(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(uploadFolder);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return new UploadResult("");
                }
            }

            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            File dest = new File(uploadFolder, fileName);
            file.transferTo(dest);

            String url = uploadBaseUrl + fileName;
            return new UploadResult(url);
        } catch (Exception e) {
            e.printStackTrace();
            return new UploadResult("");
        }
    }

    // 静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadFolder);
    }

    // 上传返回结果类
    public static class UploadResult {
        private final String url;

        public UploadResult(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    @GetMapping("/list")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/get/{pid}")
    public Product getById(@PathVariable Long pid) {
        return productService.getProductById(pid);
    }

    @GetMapping("/category/{cid}")
    public List<Product> getByCategory(@PathVariable Long cid) {
        return productService.getProductsByCid(cid);
    }

    @GetMapping("/user/{uid}")
    public List<Product> getByUser(@PathVariable Long uid) {
        return productService.getProductsByUid(uid);
    }

    @GetMapping("/status/{status}")
    public List<Product> getByStatus(@PathVariable Integer status) {
        return productService.getProductsByStatus(status);
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String keyword) {
        return productService.searchProductsByKeyword(keyword);
    }

    @PostMapping("/add")
    public String add(@RequestBody Product product) {
        System.out.println("=== 开始发布商品 ===");
        System.out.println("接收到数据：" + product.toString());

        try {
            // AI 违规检测
            String content = product.getTitle() + " " + product.getDescription();
            boolean isViolation = aiCheckService.checkViolation(content);

            if (isViolation) {
                System.out.println("=== AI 检测违规，禁止发布 ===");
                return "商品违规，无法发布";
            }

            // 正常发布
            boolean success = productService.addProduct(product);
            if (success) {
                System.out.println("=== 发布成功 ===");
                return "发布成功";
            } else {
                System.out.println("=== 服务层返回 false ===");
                return "发布失败";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "发布失败";
        }
    }

    @PutMapping("/update")
    public String update(@RequestBody Product product) {
        return productService.updateProduct(product) ? "修改成功" : "修改失败";
    }

    @GetMapping("/detail/{pid}")
    public Product getDetail(@PathVariable Long pid) {
        return productService.getProductById(pid);
    }

    @PutMapping("/status/{pid}/{status}")
    public String updateStatus(@PathVariable Long pid, @PathVariable Integer status) {
        return productService.updateProductStatus(pid, status) ? "状态修改成功" : "修改失败";
    }

    @PostMapping("/delete/{pid}")
    public String delete(@PathVariable Long pid) {
        return productService.deleteProduct(pid) ? "删除成功" : "删除失败";
    }
}