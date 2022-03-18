# addcrawler
It is an application used to collect adds from 2 Croatians most popular add web-sites, parse them, store them in local DB and send notification emails if there are new adds found or if there are some changes in existing ones.

# Setting application up
Use application.properties file to adjust properties for yourself:
* useProxy - enable proxy if needed
* proxyUrl - url of proxy
* scheduleSeconds - grep interval in seconds
* urlNjuskalo - search url for njuskalo site
* cookieNjuskalo - cookie required for njuskalo.hr requests
* userAgent - user agent header for requestsSafari/537.36
* urlIndex - search url for index oglasi
* mailSenderGmailUsername - put your gmail address here
* mailSenderGmailPassword - put your gmail application password here
* mailRecivers - put receiver addresses here, put "," even if there is only one email address

# Additional info
Application uses H2DB as Add storage database in file mode so the adds remain preserved even after application restarts. 
Database is stored within h2db folder.