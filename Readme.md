Sentisum Assessment

Tools needed to run code - JDK 11, Docker installed in your machine.

Instructions :
1. Clone code from GitHub and setup code in machine.
2. Within your machine's terminal, navigate to the project's folder.
3. Enter 'docker compose up -d' in the terminal to spin up docker containers to download ELK images.
4. Start your spring boot application in local machine.
5. Once the application is up and running, navigate to swagger ui in your browser by typing http://localhost:8080/swagger-ui/index.html
6. Use the insert api in swagger to insert data from csv files in resources folder to elastic search.
7. Go to kibana dashboard by entering localhost:5601 in your browser. You will find the index created and all the documents within it.
8. Use other APIs available in swagger to play around with them by giving different input values. 
