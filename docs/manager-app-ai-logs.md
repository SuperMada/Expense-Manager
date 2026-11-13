### AI Log For Manager App Development

Model: Claude

| Prompt | Supplemental Material | Output |
| ------ | --------------------- | ------ |
| Given the list of tables how would I enforce as many of the given rules on the SQL side so even if in SQL insert the DB will throw an error if the input data is no good | Tables + SQL rules from Project1.md | role      TEXT NOT NULL CHECK (role IN ('employee', 'manager')), amount       REAL NOT NULL CHECK (amount > 0 AND amount <= 10000),     status      TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'denied')) |
| With a variable in a .env folder how would I read that and create a DB in Java if one does not already exist in that path location and seed the data with a seed.sql file, building the DB with a schema.sql file in the same path as the seed which is 1 folder above where the db.java file is run from | N/A | See Database.java - Has been edited since |
| And given the db file how would I make sure the code doesnt fail on if the .env file isn't there or doesnt include the variable I need | N/A | The notFoundExceptions in Database.Java |
| Various debugging prompts | N/A | Issues fixed throughout the code |
| I have a java app that runs as the manager of expenses created by employees, I have the code written but I realise I have not written the most testable code, we were also given a layout example in the project details, given the code and the example layout how should I refactor my code to follow the layout and make the code more testable | Service.java, Validators.java, example layout from Project1.md | 
manager-app-java/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/revature/expensemanager/
│       │   ├── Main.java
│       │   ├── Menu.java
│       │   ├── model/
│       │   ├── service/
│       │   ├── dao/
│       │   ├── validation/
│       │   ├── db/
│       │   └── exception/
│       └── test/
│           ├── java/com/revature/expensemanager/
│           └── resources/
| ------ | --------------------- | ------ |
| And could you write a sample of what one file in each of the folders should look like to start my refactor and help me understand how each should look | N/A | Example files for each folder which helped me kickstart the refactor |