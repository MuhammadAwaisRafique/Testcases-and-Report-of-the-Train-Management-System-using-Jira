🚆 Train Ticket Reservation System

📽️ Video Guide

📌 Step-by-Step Local Setup Guide

📖 About

This project is a Train Ticket Reservation System that allows users to:
✅ View train schedules✅ Search for trains✅ Check seat availability✅ Inquire about fares✅ View trains between stations✅ Book seats online securely

🛠️ Features

🎫 Online Train Information & Reservation

🔹 View train schedules🔹 Search for trains🔹 Check seat availability🔹 Train timings and fare inquiry🔹 Secure booking system🔹 Login/logout security🔹 Password changes🔹 Payment gateway integration🔹 Ticket booking history

🛡️ Admin Access

🔹 Login🔹 Add, update, or cancel trains🔹 View train details🔹 Profile management🔹 Logout

👤 User Access

🔹 Register & login🔹 Search and view trains🔹 Check seat availability & fare🔹 Book tickets & view booking history🔹 Profile management & password change🔹 Logout

🖥️ Technologies Used

🎨 Frontend

HTML

CSS

Bootstrap

🏗️ Backend

Java (J2EE)

JDBC

Servlet

Oracle SQL

⚙️ Required Software & Tools

📌 Git📌 Java JDK 8+📌 Eclipse EE📌 Apache Maven📌 Tomcat v8.0+📌 Oracle SQL📌 Oracle SQL Developer

🏗️ Database Setup

1️⃣ Open SQL Plus OR SQL Developer

2️⃣ Login and connect to database using admin credentials

3️⃣ Create a new user:

ALTER SESSION SET "_ORACLE_SCRIPT"=TRUE;  
CREATE USER RESERVATION IDENTIFIED BY MANAGER;  
GRANT DBA TO RESERVATION;  
COMMIT;

(If error occurs, remove first line and retry.)

4️⃣ Execute SQL Tables

CONNECT RESERVATION/MANAGER;
CREATE TABLE "RESERVATION"."CUSTOMER" (
    "MAILID" VARCHAR2(40) PRIMARY KEY,
    "PWORD" VARCHAR2(20) NOT NULL,
    "FNAME" VARCHAR2(20) NOT NULL,
    "LNAME" VARCHAR2(20),
    "ADDR" VARCHAR2(100),
    "PHNO" NUMBER(12) NOT NULL
);

(Similar table creation steps for ADMIN, TRAIN, and HISTORY)

5️⃣ Insert Sample Data

INSERT INTO RESERVATION.ADMIN VALUES('admin@demo.com','admin','System','Admin','Demo Address 123 colony','9874561230');
INSERT INTO RESERVATION.TRAIN VALUES(10001,'JODHPUR EXP','HOWRAH','JODHPUR', 152, 490.50);
COMMIT;

6️⃣ Verify Tables

SELECT * FROM ADMIN;
SELECT * FROM CUSTOMER;
SELECT * FROM TRAIN;
SELECT * FROM HISTORY;

🚀 Import & Run Project in Eclipse EE

📌 Steps:

1️⃣ Open Eclipse EE (Install if not available)2️⃣ Clone the repository:https://github.com/shashirajraja/Train-Ticket-Reservation-System.git
3️⃣ Run Maven Build:

Right-click Project > Run As > Maven Build > Enter "clean install" > Apply > Run4️⃣ Configure Tomcat (if needed):

Change HTTP port to 80835️⃣ Run on Server6️⃣ Access site: http://localhost:8083/trainbook/7️⃣ Default Credentials:

Admin: admin@demo.com / admin

User: shashi@demo.com / shashi

📸 Screenshots

🔑 Login Page

✍️ Register New User

