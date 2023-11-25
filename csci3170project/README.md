## Group number: 45
    Group member    SID
    Lai Cheuk Lam	1155159309
    Wong Tuen Hung	1155158385
    AU-YEUNG, Wah	1155159862

## Prerequisites

    This project uses JDK 8 and docker for development.

## Environment Setup:

    This project can run in local environment or production environment (CSE machine).
    To change configuration of database credentials, you will need to do the following.

    For local environment:
- Go to src/client/App.java
- Uncomment the parameters "url, user, password" on line 11-13


    For production environment:
- Go to src/client/App.java
- Uncomment the parameters "url, user, password" on line 16-18

    After changing the configuration of database credentials, you can compile the script and run it by the following:
- run 'make run'

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources

- `bin`: the folder to store the compiled output files.

- `docker-compose.yaml`: Compose file which is used to configure Docker application's services on local enivronment.

For `src`, there are two folders, `client` and `salessystem`:

- client: where the user will be interacting with.
  - App.java: The entry point for the whole project. It is used to create the database connection and direct client to the sales system.

- salessystem: System where the user will do database operation as administrater, salesperson and manager.
  - Salessystem.java: Display the main menu for user to select role (admin, salesperson or manager).
  - BasicOperation.java: A general class for all role classes to validate user input and catching errors.
  - AdministratorOperation.java: Display all admin operations and allow user to create, delete, load data, and show data for database.
  - SalespersonOperation.java: Display all salesperson operations and allow user to search for parts and sell parts.
  - ManagerOperation.java: Display all manager operations and allow user to list salesperson, count sales, show total sale and popular parts.
