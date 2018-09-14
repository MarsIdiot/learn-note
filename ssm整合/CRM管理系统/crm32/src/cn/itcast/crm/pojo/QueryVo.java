package cn.itcast.crm.pojo;

/**
 *对前端数据简单的对应封装
 */
public class QueryVo {
    /*custName
        custSource
        custIndustry
        custLevel
     */
    private String custName;
    private String custSource;
    private String custIndustry;
    private String custLevel;

    /*

    分页属性
     */
    //当前页
    private  Integer page;

    //每页条数
    private  Integer size;

    //开始行=（page-1）*size
    private  Integer startRows=0;

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustSource() {
        return custSource;
    }

    public void setCustSource(String custSource) {
        this.custSource = custSource;
    }

    public String getCustIndustry() {
        return custIndustry;
    }

    public void setCustIndustry(String custIndustry) {
        this.custIndustry = custIndustry;
    }

    public String getCustLevel() {
        return custLevel;
    }

    public void setCustLevel(String custLevel) {
        this.custLevel = custLevel;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getStartRows() {
        return startRows;
    }

    public void setStartRows(Integer startRows) {
        this.startRows = startRows;
    }



}
