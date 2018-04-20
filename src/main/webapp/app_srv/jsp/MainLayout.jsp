<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>

<html lang="en" class="no-js">
  <head>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
    <meta charset="utf-8">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
    <title>
        Can I Cook
    </title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <!--<link rel="stylesheet"
      href="/PRIT/resources/app_srv/css/bootstrap.min.css" />
    <link rel="stylesheet"
      href="/PRIT/resources/app_srv/css/bootstrap-theme.min.css" /> -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    
    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    
    <link rel="stylesheet"
	href="/cic/resources/app_srv/css/custom.css" />
	<style>
	  span.label-container {
        text-align: left;
        line-height: 29px;
	  }
	  .label-input {
	    padding-top: 10px;
	  }
	  .help-block {
	    font-size: 11px;
        color: #CC3300;
      }
	</style>
	<script>
	  $(document).ready(function () {
		  $(".help-block").hide();
		  $("input").keyup(function() {
			  var id = $(this).attr("id");
			  $(".help-block-" + id).hide();
			  validateField(id);
		  });
		  $("input").change(function() {
			  var id = $(this).attr("id");
			  $(".help-block-" + id).hide();
			  validateField(id);
		  });
	  });
	  function validateField(id) {
		  var valid = true;
		  if(id == "confirmpassword") {
			  var elemId = "confirmpassword";
			  if($("#confirmpassword").val() != $("#strPassword").val()) {
				  $("#help-block-password-mismatch").show();
				  valid = false;
			  } else {
				  $("#help-block-password-mismatch").hide();
			  }
		  } else if (id == "strPassword") {
			  var capital = RegExp("[A-Z]");
			  if(!capital.test($("#strPassword").val())) {
				  $("#help-block-capital").show();
				  valid = false;
			  } else {
				  $("#help-block-capital").hide();
			  }
			  var number = RegExp("[0-9]");
			  if(!number.test($("#strPassword").val())) {
				  $("#help-block-number").show();
				  valid = false;
			  } else {
				  $("#help-block-number").hide();
			  }
		  }
		  return valid;
	  }
	  function submit() {
		  $.each($(input), function(key, value) {
			  if(!validateField($(key).attr("id"))) return false;
		  });
		  $.ajax({
			  type: "POST",
			  url: "/CIC/DuplicateUser",
			  data: {
				  "userid" : $.trim($("#userid").val())
			  },
			  success: function (response) {
				  if(!response) {
					  $("#help-block-dulpicate-userid").show();
				  }
			  },
			  error: function (error) {
				  $("#help-block-dulpicate-userid").show();
			  }
		  });
		  $.ajax({
			  type: "POST",
			  url: "/CIC/RegisterUser",
			  data: {
				  "userid" : $.trim($("#userid").val()),
				  "strPassword" : $.trim($("#strPassword").val()),
				  "firstName" : $.trim($("#firstName").val()),
				  "lastName" : $.trim($("#lastName").val()),
				  "strGender": $("input[name=strGender]:checked").val(),
				  "dateOfBirth": $("#dateOfBirth").val(),
				  "height" : $.trim($("#height").val()),
				  "weight" : $.trim($("#weight").val()),
				  "strLifeStyle": $("#strLifeStyle").val(),
				  "dietID": $("#dietID").val()
			  },
			  success: function (response) {
				  if(response) {
					  $("#success-dialog").show();
				  } else {
					  $("#error-dialog").show();
				  }
			  },
			  error: function (error) {
				  $("#error-dialog").show();
			  }
		  });
	  }
	</script>
  </head>
  <body class="text-center">
    <div class="container" style="max-width:800px;">
      <h2 class="display-2">Registration</h2>
      <form action=" " method="post" class="needs-validation" novalidate>
        <div class="panel panel-primary">
          <div class="panel-heading">Primary Details</div>
          <div class="panel-body">
            <div class="row">
              <div class="col-md-2"></div>
              <div class="col-md-7 label-input">
	            <span class="label-container"><label for="userid" class="col-md-5 col-form-label">User Name</label></span>
                <div class="col-md-7">
	              <input type="text" class="form-control" id="userid" placeholder="Username" required>
	              <span id="help-block-dulpicate-userid" class="help-block help-block-userid">Username already exists. Get creative!</span>
	            </div>
	          </div>
              <div class="col-md-2"></div>
	        </div>
            <div class="row">
              <div class="col-md-2"></div>
              <div class="col-md-7 label-input">
	            <span class="label-container"><label for="strPassword" class="col-md-5 col-form-label">Password</label></span>
                <div class="col-md-7">
                  <input type="password" class="form-control" id="strPassword" placeholder="Password" required>
                  <span id="help-block-capital" class="help-block help-block-strPassword">Sorry, your password must contain a capital letter, two numbers, a symbol, an inspiring message, a spell, a gang sign, a hieroglyph and the blood of a virgin.</span>
                  <span id="help-block-number" class="help-block help-block-strPassword">Sorry, your password is not good enough. Just like you.</span>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-2"></div>
              <div class="col-md-7 label-input">
	            <span class="label-container"><label for="confirmpassword" class="col-md-5 col-form-label">Confirm Password</label></span>
                <div class="col-md-7">
                  <input type="password" class="form-control" id="confirmpassword" placeholder="Confirm Password" required>
                  <span id="help-block-password-mismatch" class="help-block help-block-confirmpassword">Check that you used the same password in both the fields, you imbecile!</span>
                </div>
              </div>
            </div>
            <div class="col-md-2"></div>
          </div>
        </div>
         <div class="panel panel-primary">
          <div class="panel-heading">Secondary Details</div>
          <div class="panel-body">
            <div class="row">
              <div class="col-md-6 label-input">
	            <span class="label-container"><label for="firstName" class="col-md-4 col-form-label">First Name</label></span>
	            <div class="col-md-8">
	              <input type="text" class="form-control" id="firstName" placeholder="First Name" required>
	            </div>
	          </div>
              <div class="col-md-6 label-input">
	            <span class="label-container"><label for="lastName" class="col-md-4 col-form-label">Last Name</label></span>
	            <div class="col-md-8">
	              <input type="text" class="form-control" id="lastName" placeholder="Last Name">
	            </div>
	          </div>
	        </div>
            <div class="row">
              <div class="col-md-6 label-input">
                <span class="label-container"><label class="col-md-4 col-form-label">Gender</label></span>
                <div class="col-md-8">
	              <label class="col-md-6" for="male">
                    Male
                    <input class="" type="radio" name="strGender" id="male" value="male">
                  </label>
	              <label class="col-md-6" for="female">
                     Female
                    <input class="" type="radio" name="strGender" id="female" value="female">
                  </label>
                </div>
              </div>
              <div class="col-md-6 label-input">
                <span class="label-container"><label for="dateOfBirth" class="col-md-4 col-form-label">Date of Birth</label></span>
                <div class="col-md-8">
	            	<input type="date" class="form-control" id="dateOfBirth">
	            </div>
	          </div>
	        </div>
            <div class="row">
              <div class="col-md-6 label-input">
                <span class="label-container"><label for="height" class="col-md-4 col-form-label">Height</label></span>
                <div class="col-md-8">
	            	<input type="number" class="form-control" id="height" placeholder="Height in cm" min="0" max="999">
                </div>
              </div>
              <div class="col-md-6 label-input">
                <span class="label-container"><label for="weight" class="col-md-4 col-form-label">Weight</label></span>
                <div class="col-md-8">
	            	<input type="number" class="form-control" id="weight" placeholder="Weight in lbs" min="0" max="999">
                </div>
	          </div>
	        </div>
            <div class="row">
              <div class="col-md-6 label-input">
                <span class="label-container"><label for="strLifeStyle" class="col-md-4 col-form-label">Lifestyle</label></span>
                <div class="col-md-8">
                	<select class="form-control" name=strLifeStyle" id="strLifeStyle">
                	  <option value="lifestyle1">lifestyle1</option>
                	  <option value="lifestyle2">lifestyle2</option>
                	  <option value="lifestyle3">lifestyle3</option>
                	</select>
                </div>
              </div>
              <div class="col-md-6 label-input">
                <span class="label-container"><label for="dietID" class="col-md-4 col-form-label">Diet</label></span>
                <div class="col-md-8">
                	<select class="form-control" name="dietID" id="dietID">
                	  <option value="diet1">diet1</option>
                	  <option value="diet2">diet2</option>
                	  <option value="diet3">diet3</option>
                	</select>
                </div>
              </div>
	        </div>
          </div>
        </div>
	    <div class="col-4">
	    	<button type="button" class="btn btn-primary btn-lg" onclick="submit()">Register</button>
	    </div>
	    </form>
	    <div id="success-dialog" style="display:none;" class="alert alert-success">
  			<strong>Success!</strong>
  		    Redirecting you to login page.
		</div>
	    <div id="error-dialog" style="display:none;" class="alert alert-danger">
  			<strong>Error!</strong>
  			Please try again.
		</div>
      </div>
  </body>
    
 
</html>