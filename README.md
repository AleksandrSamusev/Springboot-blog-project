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

## API specification

The specification for the project can be viewed using [Swagger Editor.](https://editor.swagger.io/)
To do this, you need to go to the editor's website and import the contents of the file openapi.json


![IDEA-fragment](mainApp/src/main/resources/static/images/openapi.jpeg)


## Blog Database ER-diagram

![er-diagram](mainApp/src/main/resources/static/images/erDiagram.jpeg)

## Endpoints

### Public endpoints (accessible for unsigned users)

POST:    /api/v1/auth/register - new user registration <br>
POST:    /api/v1/auth/login - login user <br>
GET:     /api/v1/public/users - returns the list of all users <br>
GET:     /api/v1/public/users/{userId} - returns the user by given user ID <br>
GET:     /api/v1/public/articles/{articleId} - returns an article by given ID <br>
GET:     /api/v1/public/articles - returns the list of all articles <br>
GET:     /api/v1/public/articles/users/{userId} - returns all user articles by given user ID <br>
GET:     /api/v1/public/articles/tags/{tagId} - returns all articles with given tag <br>
PATCH:   /api/v1/public/articles/{articleId}/like - like an article by given ID <br>
GET:     /api/v1/public/tags/articles/{articleId} - returns list of article tags <br>
GET:     /api/v1/public/tags/{tagId} - returns a tag by given ID <br>
GET:     /api/v1/public/comments/{commentId} - returns a comment by given ID <br>
GET:     /api/v1/public/comments/articles/{articleId} - returns the list of all article comments <br>

### Private endpoints (accessible for registered users and admins)

GET:     /api/v1/private/users - returns the list of all users <br>
GET:     /api/v1/private/users/{userId} - returns the user by given user ID <br>
PATCH:   /api/v1/private/users/{userId} - update the user by given ID <br>
DELETE:  /api/v1/private/users/{userId} - delete the user by given ID <br>
POST:    /api/v1/private/articles - create an article <br>
PATCH:   /api/v1/private/articles/{articleId} - update the article by given ID <br>
GET:     /api/v1/private/articles/{articleId} - returns the article by given ID <br>
GET:     /api/v1/private/articles - returns the list of users own articles <br>
DELETE:  /api/v1/private/articles/{articleId} - delete article by given ID <br>
PATCH:   /api/v1/private/articles/{articleId}/publish - send request for article to be published <br>
POST:    /api/v1/private/tags/articles/{articleId} - create a new tag for given article <br>
PATCH:   /api/v1/private/tags/articles/{articleId}/add - add several tags to an article at ones <br>
PATCH:   /api/v1/private/tags/articles/{articleId}/remove - remove several tags from article at ones <br>
POST:    /api/v1/private/comments/articles/{articleId} - create comment to given article <br>
PATCH:   /api/v1/private/comments/{commentId} - update comment by given ID <br>
DELETE:  /api/v1/private/comments/{commentId} - delete comment by given ID <br>
POST:    /api/v1/private/messages/users/{recipientId} - send message to user with given ID <br>
GET:     /api/v1/private/messages/sent - returns all users sent messages <br>
GET:     /api/v1/private/messages/received - returns all users received messages <br>
GET:     /api/v1/private/messages/{messageId} - returns a message with given ID <br>
DELETE:  /api/v1/private/messages/{messageId} - delete message with given ID <br>

### Admin endpoints (accessible for admins only)

PATCH:   /api/v1/admin/users/{userId}/ban - ban user by given ID <br>
PATCH:   /api/v1/admin/users/{userId}/unban - unban user by given ID <br>
PATCH:   /api/v1/admin/articles/{articleId}/publish - admin confirms or rejects request for article to be published <br>
GET:     /api/v1/admin/articles/users/{authorId} - returns a list of all user articles <br>
DELETE:  /api/v1/admin/tags/{tagId} - delete tag by given ID <br>



