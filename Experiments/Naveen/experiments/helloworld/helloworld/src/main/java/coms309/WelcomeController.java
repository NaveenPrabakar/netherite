package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;

@RestController
class WelcomeController {

    /*
    changes I made:
    - Used html & css to better display screen for rest controller
    - Changed dependencies in pom.xml to support html indexes
     */

    @GetMapping("/")
    public String welcome() {

        return "<html>" + //changing the color and font
                "<head><title>Welcome</title></head>" +
                "<body>" +
                "<p style='color: #2E8B57; font-family: Verdana, Geneva, sans-serif; font-size: 36px; font-weight: bold;'>Hi My Name is Naveen Prabakar!</p>" +
                "</body></html>";

    }

    @GetMapping("/experience")
    public String wel()
    {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <title>Work Experience - Naveen Prabakar</title>" +
                "  <style>" +
                "    body {" +
                "        font-family: 'Arial', sans-serif;" +
                "        margin: 0;" +
                "        padding: 0;" +
                "        background-color: #121212;" +
                "        color: #e0e0e0;" +
                "        line-height: 1.6;" +
                "    }" +
                "    .container {" +
                "        max-width: 1200px;" +
                "        margin: 2rem auto;" +
                "        padding: 0 2rem;" +
                "        background: #1e1e1e;" +
                "        border-radius: 10px;" +
                "        box-shadow: 0 0 15px rgba(0, 0, 0, 0.3);" +
                "        padding: 2rem;" +
                "    }" +
                "    h2 {" +
                "        color: #ff8c00;" +
                "        font-size: 2rem;" +
                "        border-bottom: 2px solid #ff8c00;" +
                "        padding-bottom: 10px;" +
                "    }" +
                "    ul {" +
                "        list-style: none;" +
                "        padding: 0;" +
                "    }" +
                "    li {" +
                "        background: #2c2c2c;" +
                "        margin: 10px 0;" +
                "        padding: 15px;" +
                "        border-radius: 5px;" +
                "        border-left: 5px solid #ff8c00;" +
                "        cursor: pointer;" +
                "        transition: background 0.3s ease;" +
                "    }" +
                "    li:hover {" +
                "        background: #3a3a3a;" +
                "    }" +
                "    li h3 {" +
                "        color: #ff8c00;" +
                "        margin: 0 0 5px 0;" +
                "        font-size: 1.5rem;" +
                "    }" +
                "    li .work-details {" +
                "        display: none;" +
                "        margin-top: 10px;" +
                "    }" +
                "    li p {" +
                "        margin: 5px 0;" +
                "        font-size: 1rem;" +
                "    }" +
                "    .back-button {" +
                "        display: inline-block;" +
                "        margin: 2rem 0;" +
                "        color: #ff8c00;" +
                "        text-decoration: none;" +
                "        font-size: 1.2rem;" +
                "        background: #121212;" +
                "        padding: 10px 20px;" +
                "        border: 1px solid #ff8c00;" +
                "        border-radius: 5px;" +
                "        transition: background 0.3s ease, color 0.3s ease;" +
                "    }" +
                "    .back-button:hover {" +
                "        background: #ff8c00;" +
                "        color: #121212;" +
                "    }" +
                "  </style>" +
                "  <script>" +
                "    document.addEventListener('DOMContentLoaded', () => {" +
                "        const workItems = document.querySelectorAll('li');" +
                "        workItems.forEach(item => {" +
                "            item.addEventListener('click', () => {" +
                "                const details = item.querySelector('.work-details');" +
                "                if (details.style.display === 'block') {" +
                "                    details.style.display = 'none';" +
                "                } else {" +
                "                    details.style.display = 'block';" +
                "                }" +
                "            });" +
                "        });" +
                "    });" +
                "  </script>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "  <h2>Work Experience</h2>" +
                "  <ul>" +
                "    <li>" +
                "      <h3>Iowa State University Help Room, Lead Computer Science Tutor, Ames, Iowa</h3>" +
                "      <div class=\"work-details\">" +
                "        <p><strong>Duration:</strong> 08/2024 - Current</p>" +
                "        <ul>" +
                "          <li>Will lead and orchestrate small group tutoring sessions of 4-5 people</li>" +
                "          <li>Will tutor six different courses: Data Structures, Object-Oriented Programming, Python, Discrete Mathematics, Computer Architecture, and Algorithms, providing flexibility and range to clients</li>" +
                "        </ul>" +
                "      </div>" +
                "    </li>" +
                "    <li>" +
                "      <h3>Iowa State University Help Room, Computer Science Tutor, Ames, Iowa</h3>" +
                "      <div class=\"work-details\">" +
                "        <p><strong>Duration:</strong> 08/2023 - 05/2024</p>" +
                "        <ul>" +
                "          <li>Supported over 15-20 students in understanding and implementing essential data structures such as arrays, linked lists, stacks, queues, and binary trees, resulting in a 10-15% improvement in their exam performance</li>" +
                "          <li>Assisted more than 30 students in mastering object-oriented programming in Java, leading to a 10-20% increase in their project completion rates</li>" +
                "          <li>Guided over 20 students through fundamental programming concepts and problem-solving techniques in Python, resulting in a 20% improvement in their assignment scores</li>" +
                "        </ul>" +
                "      </div>" +
                "    </li>" +
                "  </ul>" +
                "</div>" +
                "</body>" +
                "</html>";
    }//displays experience template

    @GetMapping("/projects")
    public String pro(){

        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Projects - Naveen Prabakar</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: 'Arial', sans-serif;" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            background-color: #121212;" +
                "            color: #e0e0e0;" +
                "            line-height: 1.6;" +
                "        }" +
                "        .container {" +
                "            max-width: 1200px;" +
                "            margin: 2rem auto;" +
                "            padding: 0 2rem;" +
                "            background: #1e1e1e;" +
                "            border-radius: 10px;" +
                "            box-shadow: 0 0 15px rgba(0, 0, 0, 0.3);" +
                "            padding: 2rem;" +
                "        }" +
                "        h2 {" +
                "            color: #ff8c00;" +
                "            font-size: 2rem;" +
                "            border-bottom: 2px solid #ff8c00;" +
                "            padding-bottom: 10px;" +
                "        }" +
                "        ul {" +
                "            list-style: none;" +
                "            padding: 0;" +
                "        }" +
                "        li {" +
                "            background: #2c2c2c;" +
                "            margin: 10px 0;" +
                "            padding: 15px;" +
                "            border-radius: 5px;" +
                "            border-left: 5px solid #ff8c00;" +
                "            cursor: pointer;" +
                "            transition: background 0.3s ease;" +
                "        }" +
                "        li:hover {" +
                "            background: #3a3a3a;" +
                "        }" +
                "        li h3 {" +
                "            color: #ff8c00;" +
                "            margin: 0 0 5px 0;" +
                "            font-size: 1.5rem;" +
                "        }" +
                "        li .project-details {" +
                "            display: none;" +
                "            margin-top: 10px;" +
                "        }" +
                "        li p {" +
                "            margin: 5px 0;" +
                "            font-size: 1rem;" +
                "        }" +
                "        li a {" +
                "            color: #ff8c00;" +
                "            text-decoration: none;" +
                "            transition: color 0.3s ease;" +
                "        }" +
                "        li a:hover {" +
                "            color: #ffffff;" +
                "        }" +
                "        .back-button {" +
                "            display: inline-block;" +
                "            margin: 2rem 0;" +
                "            color: #ff8c00;" +
                "            text-decoration: none;" +
                "            font-size: 1.2rem;" +
                "            background: #121212;" +
                "            padding: 10px 20px;" +
                "            border: 1px solid #ff8c00;" +
                "            border-radius: 5px;" +
                "            transition: background 0.3s ease, color 0.3s ease;" +
                "        }" +
                "        .back-button:hover {" +
                "            background: #ff8c00;" +
                "            color: #121212;" +
                "        }" +
                "    </style>" +
                "    <script>" +
                "        document.addEventListener('DOMContentLoaded', () => {" +
                "            const projectItems = document.querySelectorAll('li');" +
                "            projectItems.forEach(item => {" +
                "                item.addEventListener('click', () => {" +
                "                    const details = item.querySelector('.project-details');" +
                "                    if (details.style.display === 'block') {" +
                "                        details.style.display = 'none';" +
                "                    } else {" +
                "                        details.style.display = 'block';" +
                "                    }" +
                "                });" +
                "            });" +
                "        });" +
                "    </script>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "    <h2>Projects</h2>" +
                "    <ul>" +
                "        <li>" +
                "            <h3>Machine Code Translator</h3>" +
                "            <div class=\"project-details\">" +
                "                <p>- Translated machine code into 32-bit binary numbers for the program to use.</p>" +
                "                <p>- Implemented bitwise operations and bit manipulation on the 32-bit numbers to translate the binary numbers to readable legv8 assembly instructions.</p>" +
                "                <p>- Created Shell scripts for the program to run through.</p>" +
                "                <p><strong>Skills:</strong> Machine Code · Shell Scripting · Computer Science · Java · Computer Architecture · Assembly Language · Object-Oriented Programming (OOP)</p>" +
                "                <p><a href=\"https://github.com/NaveenPrabakar/Com-s-321-computer-architecture-\" target=\"_blank\">View Project</a></p>" +
                "            </div>" +
                "        </li>" +
                "        <li>" +
                "            <h3>Coding Journal</h3>" +
                "            <div class=\"project-details\">" +
                "                <p>- Engineered a Python backend program that efficiently stored user data entries into a MySQL database, categorized by user input; improved data retrieval speed by 15%-20%.</p>" +
                "                <p>- Designed a sophisticated application that customized data outputs according to user preferences.</p>" +
                "                <p>- Enabled user-driven modification of data entries through customized insert statements, enhancing user experience with greater accessibility and flexibility.</p>" +
                "                <p>- Implemented APIs to create an automatic spell and grammar checker for user entries.</p>" +
                "                <p>- Created a shell script to run the program.</p>" +
                "                <p><strong>Skills:</strong> Databases · Python (Programming Language) · Database Management System (DBMS) · MySQL · SQL · Query Optimization</p>" +
                "                <p><a href=\"https://github.com/NaveenPrabakar/CodingJournal\" target=\"_blank\">View Project</a></p>" +
                "            </div>" +
                "        </li>" +
                "        <li>" +
                "            <h3>MYSQL and NEO4j Database Management</h3>" +
                "            <div class=\"project-details\">" +
                "                <p>- Created structured Entity-Relationship diagrams for organizational databases.</p>" +
                "                <p>- Successfully imported CSV files into designated tables in both Neo4j and MySQL environments, ensuring data integrity and consistency of 95%.</p>" +
                "                <p>- Developed and optimized query statements for enhanced efficiency and performance, contributing to quicker outputs.</p>" +
                "                <p><strong>Skills:</strong> Databases · Database Management System (DBMS) · MySQL · SQL · Neo4j · Query Optimization · CSV file</p>" +
                "                <p><a href=\"https://github.com/NaveenPrabakar/Com-S-363-DBMS-\" target=\"_blank\">View Project</a></p>" +
                "            </div>" +
                "        </li>" +
                "        <li>" +
                "            <h3>Discord Trivia Bot</h3>" +
                "            <div class=\"project-details\">" +
                "                <p>- Engineered a highly accurate Discord bot for handling trivia quizzes with score tracking, leaderboard features, and multi-server support; achieved 99% accuracy and increased user engagement by 10-15%.</p>" +
                "                <p>- Utilized Docker for containerization to ensure continuous operation with minimal downtime.</p>" +
                "                <p>- Integrated MySQL database to store and manage user data, ensuring reliable and scalable data handling; stored and retrieved quiz questions, ensuring organized data management and efficient question extraction.</p>" +
                "                <p>- Used Discord UI to create buttons (for choice answers, next options) to ensure a user-friendly gameplay experience.</p>" +
                "                <p>- Utilized Google Translate API to provide flexibility and range to users; giving them choices to switch to over 25 languages.</p>" +
                "                <p><strong>Skills:</strong> MySQL · Database Management System (DBMS) · Docker · Discord API · Google Translate API · Backend Development</p>" +
                "                <p><a href=\"https://github.com/NaveenPrabakar/Discord-Trivia-Bot-\" target=\"_blank\">View Project</a></p>" +
                "            </div>" +
                "        </li>" +
                "    </ul>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

