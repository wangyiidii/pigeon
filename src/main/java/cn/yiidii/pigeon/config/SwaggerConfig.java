package cn.yiidii.pigeon.config;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger 配置类
 */
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {

    static {
        log.info("init SwaggerConfig...");
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select().apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                //需要扫描生成swagger文档接口的包路径，注意别写错了，错了swagger页面打开就不会有接口再上面
                //.apis(RequestHandlerSelectors.basePackage("cn.yiidii.panel.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    //api文档的一些页面基本信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("Panel Swagger API Doc")
                //作者的相关信息
                .contact(new Contact("yiidii", "http://yiidii.cn", "1141309981@qq.com"))
                //版本号
                .version("1.0")
                //详细描述
                .description("panel restful 接口文档")
                .build();
    }


}
