package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    //HttpServletRequest的作用是，待会登录成功后我们要将Employee对象中的id存到session中，用request来get一个session
    //登录请求中的request payload中的username和password和实体类Employee的属性名相同，SpringMVC自动填充
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //Employee::getUsername是MP实现的获取username对应的数据库中的字段名，这样不容易写错，也很优雅
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //根据 Wrapper，查询一条记录
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null){
            //结果类R中的success和error都是静态方法，可以不用new对象直接引用
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        HttpSession session = request.getSession();
        session.setAttribute("employee", emp.getId());//向session域共享数据

        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1、清理Session中保存的的当前员工的id
        //当时保存id到session时，设置的name是什么，删除时也用同样的name
        request.getSession().removeAttribute("employee");

        //2、返回结果
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @RequestBody 注解是因为前端发送请求所带的JSON数据存储在Request Payload中
     * 形参Employee employee是SpringMVC会自动将Request Payload中的数据与实体类的属性进行填充
     * request用来获得当前注册新员工的账号的id
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息：{}", employee.toString());
        //设置初始密码123456，进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");

//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        //线程id
        long id = Thread.currentThread().getId();
        log.info("线程id:{}",id);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工分页查询
     * @param page  当前页数
     * @param pageSize 当前页最多存放数据条数,就是这一页查几条数据
     * @param name 根据name查询员工的信息
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //这里之所以是返回page对象(mybatis-plus的page对象)，是因为前端需要这些分页的数据(比如当前页，总页数)
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        //创建分页构造器
        Page pageInfo = new Page(page, pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加构造过滤条件
        queryWrapper.like(Strings.isNotEmpty(name), Employee::getName, name);

        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee 请求中包含id和status，使用@RequestBody注释后SpringMVC将数据自动填充到实体类中
     * @return 前端只需要res.code等于0或1，所以直接返回R的string泛型
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //线程id
        long id = Thread.currentThread().getId();
        log.info("线程id:{}",id);

        log.info(employee.toString());

        //更新修改时间
//        employee.setUpdateTime(LocalDateTime.now());
        //更新当前修改人的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);

        //使用service方法更新员工信息
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id 员工id
     * @return 返回查询到的员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

}
