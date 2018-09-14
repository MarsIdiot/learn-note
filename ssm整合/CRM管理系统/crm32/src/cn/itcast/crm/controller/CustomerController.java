package cn.itcast.crm.controller;

import cn.itcast.crm.pojo.BaseDict;
import cn.itcast.crm.pojo.Customer;
import cn.itcast.crm.pojo.QueryVo;
import cn.itcast.crm.service.BaseDictService;
import cn.itcast.crm.service.CustomerService;
import cn.itcast.crm.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("customer")
public class CustomerController {

    @Autowired
    private BaseDictService baseDictService;

    @Autowired
    private CustomerService customerService;

    @Value("${fromType.code}")
    private String fromTypeCode;

    @Value("${industryType.code}")
    private String industryTypeCode;

    @Value("${levelType.code}")
    private String levelTypeCode;

    /**
     * 查询列表
     * @param model
     * @return
     */
    @RequestMapping("list")
    public  String list(Model model, QueryVo vo){

        /*
            数据字典的获取
         */
        List<BaseDict> fromType = baseDictService.queryBaseDictByDictTypeCode(fromTypeCode);
        List<BaseDict>  industryType= baseDictService.queryBaseDictByDictTypeCode(industryTypeCode);
        List<BaseDict> levelType = baseDictService.queryBaseDictByDictTypeCode(levelTypeCode);

        model.addAttribute("fromType",fromType);
        model.addAttribute("industryType",industryType);
        model.addAttribute("levelType",levelType);

        /*
            分页对象的获取
         */
        Page<Customer> page = customerService.selectPageByQueryVo(vo);
        model.addAttribute("page",page);

        /*
            数据回显
                private String custName;
                private String custSource;
                private String custIndustry;
                private String custLevel;
         */
        model.addAttribute("custName",vo.getCustName());
        model.addAttribute("custSource",vo.getCustSource());
        model.addAttribute("custIndustry",vo.getCustIndustry());
        model.addAttribute("custLevel",vo.getCustLevel());


        return "customer";
    }

    /**
        跳转到修改页面，回显数据
     */
    @RequestMapping("edit")
    public  @ResponseBody Customer toEdit(Integer id){
        Customer customer=customerService.selectCustomerById(id);
        return customer;
    }

    /**
    update  更新用户列表
     */
    @RequestMapping("update")
    public  @ResponseBody String update(Customer customer){
        customerService.updateCustomerByCustomer(customer);
        return "OK";
    }

    /**
    delete 根据id删除用户
     */
    @RequestMapping("delete")
    public  @ResponseBody String delete(Integer id){
        customerService.deleteCustomerById(id);
        return "OK";
    }


}
