1. Spring security adds the security filter b/w the requests and the responds.
2. Hence every request goes through this security filter
3. Session id is provided when the user logins into the system, same session id is used for different pages unless the session id is deleted

Security Chain

CSRF Token
- unthorized user steals the session id and send request to server
- Headers - (X-CSRF-TOKEN)
   - FOR PUT, POST  

- SameSite=Strict, the browser will ONLY send the cookie when the user is:
- On the same site/domain, 
- And not coming from another website.