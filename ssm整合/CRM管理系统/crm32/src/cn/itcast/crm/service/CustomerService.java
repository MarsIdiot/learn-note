package cn.itcast.crm.service;

import cn.itcast.crm.pojo.Customer;
import cn.itcast.crm.pojo.QueryVo;
import cn.itcast.crm.utils.Page;

public interface CustomerService {

    Page<Customer>  selectPageByQueryVo(QueryVo vo);

    /**
        根据用户id查询用户
     */
    Customer selectCustomerById(Integer id);

    /**
    update  更新用户列表
     */
    void updateCustomerByCustomer(Customer customer);


    /**
     delete 根据id删除用户
     */
    void deleteCustomerById(Integer id);
}
