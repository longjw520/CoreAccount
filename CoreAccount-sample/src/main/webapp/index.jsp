<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="shortcut icon" href="${ctx}/static/images/favicon.ico" type="image/x-icon">
  <link rel="stylesheet" type="text/css" href="${ctx}/static/bootstrap/2.2.2/css/bootstrap.min.css">
  <link rel="stylesheet" type="text/css" href="${ctx}/static/jquery-validation/1.10.0/validate.css">
  <link rel="stylesheet" type="text/css" href="${ctx}/static/styles/default.css">
  <script type="text/javascript" src="${ctx}/static/jquery/jquery-1.8.3.min.js">
  </script>
  <script type="text/javascript" src="${ctx}/static/jquery-validation/1.10.0/jquery.validate.min.js">
  </script>
  <script type="text/javascript" src="${ctx}/static/jquery-validation/1.10.0/messages_bs_zh.js">
  </script>
  <script type="text/javascript" src="${ctx}/static/mergely/codemirror.js"">
  </script>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/styles/codemirror.css">

  <script type="text/javascript" src="${ctx}/static/mergely/mergely.js"">
  </script>
  <link rel="stylesheet" type="text/css" href="${ctx}/static/styles/mergely.css">
  <title>
    报文请求测试
  </title>
  <script>
  $(document).ready(function () {
        //聚焦第一个输入框
        $("#url").focus();
        //为inputForm注册validate函数
        $("#inputForm").validate();

        $('#compare').mergely({
          cmsettings: { readOnly: false, lineNumbers: true }

        });
      });

  function messageSub() {
    $("#result_rmi").html("");
    var url = $("#url").val();
    var mes_con = $("#message_content").val();
    if(url == "" || mes_con == ""){
      alert("请填写每个属性！");
      return;
    }

    $.ajax({
      type: "POST",
      url: "sendDatagram",
      data: { url: url, content: mes_con}
    }).success(function (data) {
      $("#result_rmi").html(data);
    }).error(function (data) {
      $("#result_rmi").html(data);
    }
    );
  }

  function jsonSub() {
    $("#result_json").html("");
    var jsonStrCon = $("#jsonStr").val();
    if (jsonStrCon == null || jsonStrCon == "") {
      alert("请输入非空Json字符串！");
      return;
    }
    $.ajax({
      type: "POST",
      url: "jsonFormat",
      data: { jsonStr: jsonStrCon}
    }).success(function (data) {
      $("#result_json").html(data);
    }).error(function (data) {
      $("#result_json").html(data);
    }
    );
  }
  </script>
  <!--<script>
  $(function(){
    $("#btn_encry").click(function(){
      $("#result").html("");
      $.get("encry", { plaintext:$("#plaintext").val() },
        function(data){
          $("#result").html(data);
        });
    });
  })
  </script>-->

</head>

<body>
  <div class="tabbable">
    <ul class="nav nav-tabs">
      <li class="active">
        <a href="#tab1" data-toggle="tab">
          通用报文
        </a>
      </li>
      <li>
        <a href="#tab2" data-toggle="tab">
          Json 格式化
        </a>
      </li>
      <!-- <li>
        <a href="#tab3" data-toggle="tab">
          加密解密
        </a>
      </li> -->
      <li>
        <a href="#tab4" data-toggle="tab">
          文本对比
        </a>
      </li>
    </ul>
    <div class="tab-content">
      <div class="tab-pane active" id="tab1">
        <form id="inputForm" action="${ctx}/sendDatagram" method="post" class="form-horizontal">

          <div class="control-group">
            <label for="url" class="control-label">
              rmi url:
            </label>

            <div class="controls">
              <input type="text" id="url" name="url" class="input-large required" minlength="3" style="width:80%"/>

            </div>
          </div>
          <div class="control-group">
            <label class="control-label">
              报文内容:
            </label>

            <div class="controls">
              <textarea 
              id="message_content"
              name="content"
              class="input-large required"
              minlength="10"
              style="width:80%;height:500px;">
            </textarea>
          </div>
        </div>
        <div class="form-actions">
          <input id="submit_btn" class="btn btn-primary" type="button" onclick="messageSub()" value="提交"/>
          &nbsp;
          <input class="btn btn-primary"  type="reset" value="重置">
        </div>
        <pre id="result_rmi">
        </pre>
      </form>
    </div>
    <div class="tab-pane" id="tab2">
      <form id="jsonFormatForm" action="${ctx}/jsonFormat" method="post" class="form-horizontal">
        <legend>
          <small>
            请输入Json字符串
          </small>
        </legend>
        <div class="control-group">
          <div class="controls">
            <textarea id="jsonStr" name="jsonStr" style="width:80%;" class="input-large required">
              ${jsonStr}
            </textarea>
          </div>
        </div>
        <div class="form-actions">
          <input id="submit_btn_" class="btn btn-primary" type="button" onclick="jsonSub()" value="提交"/>
          &nbsp;
          <input class="btn btn-primary"  type="reset" value="重置">
        </div>
      </form>
      <pre id="result_json">
      </pre>
    </div>

    <!-- <div class="tab-pane" id="tab3">
      <form method="post" action="${ctx}/encry" class="form-horizontal">
        <div class="controls">
          请输入要加密的字符(明文)：
          <input type="text" id="plaintext" name="plaintext" class="input-large required" style="width:20%"/>
        </div>
        <input id="btn_encry" class="btn btn-primary" type="button" value=" 提  交 ">
      </form>
      <pre id="result">
      </pre>
    </div>-->
    <div class="tab-pane" id="tab4">
      
        
            <div id="compare"><div>
       
     
         

      </div>
    </div>
  </div>
</body>
</html>
