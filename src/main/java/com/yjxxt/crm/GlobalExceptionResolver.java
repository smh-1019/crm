package com.yjxxt.crm;

import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {

        if (ex instanceof NoLoginException){
            ModelAndView modelAndView = new ModelAndView("redirect:/index");
            return modelAndView;
        }

        ModelAndView mov = new ModelAndView("error");
        mov.addObject("code", "400");
        mov.addObject("msg", "参数异常");
        // 判断 HandlerMethod
        if (handler instanceof HandlerMethod) {
            // 类型转换
            HandlerMethod hm = (HandlerMethod) handler;
            // 获取方法上的 ResponseBody 注解
            ResponseBody responseBody = hm.getMethod().getDeclaredAnnotation(ResponseBody.class);
            // 判断 ResponseBody 注解是否存在 (如果不存在，表示返回的是视图;如果存在，表示 返回的是JSON)
            if (responseBody == null) {
                if (ex instanceof ParamsException) {
                    ParamsException pe = (ParamsException) ex;
                    ResultInfo resultInfo = new ResultInfo();
                    mov.addObject("code", pe.getCode());
                    mov.addObject("msg", pe.getMsg());
                }
                return mov;
            }else {
                /*** 方法上返回JSON */
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("参数异常");
                // 如果捕获的是自定义异常
                if (ex instanceof ParamsException) {
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                // 设置响应类型和编码格式 （响应JSON格式）
                resp.setContentType("application/json;charset=utf-8");
                // 得到输出流
                PrintWriter pw = null;
                try {
                    pw = resp.getWriter();
                    // 将对象转换成JSON格式，通过输出流输出 响应给请求的前台
                    pw.write(JSON.toJSONString(resultInfo));
                    pw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (pw == null) {
                        pw.close();
                    }

                }
                return null;
            }
        }
        return mov;
    }

}


