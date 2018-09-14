package cn.itcast.crm.service.impl;

import cn.itcast.crm.mapper.CustomerMapper;
import cn.itcast.crm.pojo.Customer;
import cn.itcast.crm.pojo.QueryVo;
import cn.itcast.crm.service.CustomerService;
import cn.itcast.crm.utils.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {


    @Autowired
    private CustomerMapper customerMapper;



    public  Page<Customer> selectPageByQueryVo(QueryVo vo){

        /*
            组装Page<Customer>对象
         */
        Page<Customer> page=new Page<Customer>();
        //每页数
        page.setSize(10);
        vo.setSize(10);
        if(vo!=null){
            // 判断当前页
            if (null != vo.getPage()) {
                page.setPage(vo.getPage());
                vo.setStartRows((vo.getPage() -1)*vo.getSize());
            }
            if(vo.getCustName()!=null&&!vo.getCustName().trim().equals("")){
                vo.setCustName(vo.getCustName().trim());
            }
            if(null != vo.getCustSource() && !"".equals(vo.getCustSource().trim())){
                vo.setCustSource(vo.getCustSource().trim());
            }
            if(null != vo.getCustIndustry() && !"".equals(vo.getCustIndustry().trim())){
                vo.setCustIndustry(vo.getCustIndustry().trim());
            }
            if(null != vo.getCustLevel() && !"".equals(vo.getCustLevel().trim())){
                vo.setCustLevel(vo.getCustLevel().trim());
            }
            //获取总数和结果集
            Integer total = this.customerMapper.selectPageTotalByQueryVo(vo);
            page.setTotal(total);
            List<Customer> customerPage = customerMapper.selectPageCustomerByQueryVo(vo);
            page.setRows(customerPage);

        }

        return  page;
    }

    /*
        根据用户id查询用户
     */
    @Override
    public Customer selectCustomerById(Integer id) {
        return customerMapper.selectCustomerById(id);
    }


    /**
     update  更新用户列表
     */
    @Override
    public void updateCustomerByCustomer(Customer customer) {
        customerMapper.updateCustomerByCustomer(customer);
    }


    /**
     delete 根据id删除用户
     */
    @Override
    public void deleteCustomerById(Integer id) {
        customerMapper.deleteCustomerById(id);
    }
}
