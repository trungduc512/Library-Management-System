
# **Library Management System**

## **Introduction**  
The **Library Management System** is a Java-based application designed to simplify library operations. It allows librarians and borrowers to efficiently manage documents, borrowing/returning processes, and organize library resources.

The system supports two types of users:  
1. **Borrower**  
2. **Librarian**  

---

## **Key Features**

### **Borrower**
- Search for documents by ISBN or title. 
- Borrow documents and manage borrowed items.  
- Rate and review documents after usage.  
- Receive notifications from the library.  

### **Librarian**
- Add, edit, and manage document inventory.  
- Manage borrowers. 
- Generate library reports and statistics.  

---

## **Installation**

### **1. System Requirements**
- **Programming Language**: Java (JDK 11 or later).  
- **IDE**: IntelliJ IDEA, Eclipse, or NetBeans.  
- **Database**: MySQL. 

### **2. Setup Instructions**
1. Clone the repository:  
   ```bash
   git clone https://github.com/trungduc512/Library-Management-System.git
   ```  
2. Open the project in your IDE and configure the database settings in the `application.properties` (or `config.properties`) file.  
3. Build the project:  
   ```bash
   mvn clean install
   ```  
4. Run the application:  
   ```bash
   java -jar target/library-management-system.jar
   ```  

---

## **Usage**

### **Common Features**
1. **Login**:  
   - Enter your username and password to access the system.  
   - The system automatically assigns the interface based on user roles (Borrower or Librarian).  
2. **Home Page**:  
   - Displays an overview of the library, including featured documents, and notifications.  
3. **Search Documents**:  
   - Search for documents by **ISBN**, title.
   - Results display document availability and details.  

---

### **1. Borrower**

- **Borrow Document**:  
  - Search for the desired document.  
  - If available, click **Borrow** to send a request.   

- **Rate Document**:  
  - Leave a rating or review for documents you have read.  
  - Reviews are visible to other users.  

- **Manage Borrowed Documents**:  
  - View the list of borrowed documents and their statuses (e.g., borrowed, overdue).  
  - Click **Return Document** to complete the return process.  

---

### **2. Librarian**

- **Add Document**:  
  - Fill in document details (ISBN, title, author, category, quantity).  
  - Click **Save** to add the new document.  

- **Edit Document Quantity**:  
  - Search for the document you want to update.  
  - Click **Edit**, update the quantity, and save changes.  

- **Manage Borrowers**:  
  - View the list of registered Borrowers.   
  - Track each Borrower’s borrowing/return history.  

---

## **Application Architecture**

- **Language**: Java  
- **Framework**: JavaFX for the GUI.  
- **Database**: MySQL.  
- **Design Pattern**: MVC (Model-View-Controller) for modular and scalable code organization.  

---

## **Contributing**  
If you wish to contribute to the project:  
1. Fork this repository.  
2. Create a new branch for your changes:  
   ```bash
   git checkout -b feature/your-feature-name
   ```  
3. Create a Pull Request to the main repository.  

---

## **Contact**
- **Author**: Do Trung Duc  
  **Student ID**: 23020045  
  **Email**: 23020045@vnu.edu.vn  
- **Author**: Vu Hai Dang  
  **Student ID**: 23020012  
  **Email**: 23020012@vnu.edu.vn  
- **Author**: Nguyen Trong Hieu  
  **Student ID**: 23020069  
  **Email**: 23020069@vnu.edu.vn   


