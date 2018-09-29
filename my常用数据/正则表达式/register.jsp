<%--
  Created by IntelliJ IDEA.
  User: ucarinc
  Date: 2018/7/19
  Time: 14:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%--<c:set var="_ctx" value="${pageContext.request.contextPath}"></c:set>--%>
<html>
<head>
    <title>register</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.min.js"></script>
    <script type="text/javascript">
        var flag = {
            "userName":false,
            "trueName":false,
            "password":false,
        };

        $(function(){
            $("#userName").blur(function(){
                // 用户名校验
                var userName = $(this).val();

                if(userName==''){
                    $("#userName\\.info").html("用户名不能为空");
                    return;
                }else if(userName.length<3 || userName.length>12){
                    $("#userName\\.info").html("用户名需在3-12位之间");
                    return;
                }else {
                    $("#userName\\.info").html("");
                    flag.userName = true;
                }

            });

            $("#trueName").blur(function(){
                // 昵称校验
                var trueName = $(this).val();

                if(userName==''){
                    $("#userName\\.info").html("用户名不能为空");
                    return;
                }else if(userName.length<3 || userName.length>12){
                    $("#userName\\.info").html("用户名需在3-12位之间");
                    return;
                }else {
                    $("#userName\\.info").html("");
                    flag.userName = true;
                }
            });

            // 密码校验
            $("#password").blur(function(){
                var password=$(this).val();

                var pattern = /\b(^['A-Za-z0-9]{6,12}$)\b/;
                if (!pattern.test(password)) {
                    $("#password\\.info").html("格式有误，请输入6~12位的数字、字母！");
                    return;
                }else{
                    $("#password\\.info").html("");
                    //flag.password=true;
                    return;
                }
            });


            // 密码重复校验
            $("#repeatPassword").blur(function(){
                var repeatPassword = $(this).val();

                var pattern = /\b(^['A-Za-z0-9]{4,20}$)\b/;
                if (repeatPassword!=$("#password").val()) {
                    $("#repeatPassword\\.info").html("两次密码输入不一致");
                    return;
                }else{
                    $("#repeatPassword\\.info").html("");
                    flag.password = true;
                    return;
                }
            });

            $("#registerFrom").submit(function(){
                var ok = flag.password&&flag.trueName&&flag.userName;
                if(ok==false){
                    alert("表单项正在检测或存在错误");
                    history.back();
                    return false;
                }
                return true;
            });
        });


    </script>
</head>
<body>
    <form name="registerFrom" id="registerFrom" action="${pageContext.request.contextPath }/register.do" method="post" enctype="multipart/form-data" style="border: black solid 1px;width: 300px;text-align: center" >
        <div>
            <label>用户名：</label>
            <input type="text"  id="userName" name="username"
                   placeholder="请输入用户名"   class="form-control" required>
            <span id="userName.info" style="color:red"></span>
        </div>
        <div>
            <label>密码：</label>
            <input type="text"  id="password" name="password"
                   placeholder="请输入密码" class="form-control" required>
            <span id="password.info" style="color:red"></span>
        </div>
        <div>
            <label>重复密码：</label>
            <input type="text"  id="repeatPassword" name="repeatPassword"
                   placeholder="请再次输入密码" class="form-control" required>
            <span id="repeatPassword.info" style="color:red"></span>
        </div>
        <div>
            <label>姓名：</label>
            <input type="text"  id="trueName" name="trueName"
                   placeholder="请输入姓名" class="form-control" required>
            <span id="trueName.info" style="color:red"></span>
        </div>
        <div>
            <label>头像：</label>
            <input type="file"  name="pictureFile"/><br/>
        </div>

        <input type="submit" value="注册" name="register" id="register"/>
    </form>
</body>
</html>
