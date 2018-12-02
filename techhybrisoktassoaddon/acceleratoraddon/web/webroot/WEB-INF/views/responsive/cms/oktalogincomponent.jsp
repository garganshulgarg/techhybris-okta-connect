<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<script src="https://ok1static.oktacdn.com/assets/js/sdk/okta-signin-widget/2.5.0/js/okta-sign-in.min.js" type="text/javascript"></script>
<link href="https://ok1static.oktacdn.com/assets/js/sdk/okta-signin-widget/2.5.0/css/okta-sign-in.min.css" type="text/css" rel="stylesheet">
<!-- Optional, customizable css theme options. Link your own customized copy of this file or override styles in-line -->
<link href="https://ok1static.oktacdn.com/assets/js/sdk/okta-signin-widget/2.5.0/css/okta-theme.css" type="text/css" rel="stylesheet">
<!-- Render the login widget here -->
<div id="okta-login-container"></div>
<spring:eval expression="@hybrisProperties.getProperty('okta.techhybris.baseUrl')" var="orgUrl" />

<!-- Script to init the widget -->
<!-- Below mentioned Scripts is highly configurable and CSS specific to site can be applied -->
<!-- Refer https://developer.okta.com/code/javascript/okta_sign-in_widget for more details.-->
<!--  Currently for the addon purpose we are using default OKTA Theme -->
<!-- Login Form is only visible to Anonymous customer. If some other behaviour is expected do customization over here -->

<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
    <script>
      var orgUrl = '${orgUrl}';
      var oktaSignIn = new OktaSignIn({baseUrl: orgUrl});

      oktaSignIn.renderEl(
        { el: '#okta-login-container' },
        function (res) {
          var response= JSON.stringify(res);
          if (res.status === 'SUCCESS') {
          var loginId = res.user.profile.login;
          var sessionToken = res.session.token;
          $.ajax({url: '${encodedContextPath}/oktasso/login',
                     data: {uid:loginId,sToken:sessionToken},
                     type: 'POST',
                     success: function(result){
                       res.session.setCookieAndRedirect(result.redirectionURL);
                       }
          });

          }
        });
    </script>
</sec:authorize>