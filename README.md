# My Blog service

**My Blog is a service for creating, searching and reading articles by various authors. 
The service available to both registered and unregistered users.**

## Features

The main features of the service are as follows:

**Unregistered users**:

1. Search and view articles;
2. Like the article;
3. View information about the author of the article;
4. Sort articles (most views, most likes, articles for a certain period, newest, oldest, etc.);
5. Clicking on an article tag displays all articles that have current tag;
6. View articles with pagination;
7. View all articles by the author;
8. Registration on the service;
9. Login using users credentials;

**Registered users**:

1. Create a new article;
2. Comment on articles;
3. Publish the created article (admin conformation required);
4. Edit personal information;
5. Edit own article (re-confirmation is required after editing);
6. View the status of the own article (published/not published);
7. View all own published/unpublished articles;
8. Delete own article (the article is deleted from the DB);
9. Remove own article from publication (likes and reviews resets to zero);
10. Delete own account;
11. Log out;

**Admin**:

1. Delete a user;
2. Delete an article;
3. Remove an article from publication (the reason should be visible to the author of the article);
4. Confirm/reject the publication of the article (in case of rejection the author sees admins comments);
5. Block the user from leaving comments on articles; 

