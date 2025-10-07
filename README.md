# Food Delivery Management System

A **Java Swing-based Food Delivery Management System** that handles the complete workflow from customer ordering to driver delivery. This is a multi-role application with separate interfaces for Customers, Drivers, and Schedulers, demonstrating full-stack Java development with database integration and role-based access control.

---

## Table of Contents
- [Features](#features)
- [System Architecture](#system-architecture)
- [Workflow](#workflow)
- [Database Structure](#database-structure)
- [Technical Stack](#technical-stack)
- [Installation](#installation)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **Multi-role Support**: Customer, Driver, Scheduler roles with customized interfaces.
- **Order Management**: Complete lifecycle from order creation to delivery.
- **Database Integration**: Persistent storage using MySQL.
- **JSON Data Handling**: Food items stored as JSON for flexibility.
- **Input Validation**: Phone number formatting, numeric filters, and required fields.
- **Real-time Updates**: Track order status and delivery progress.
- **Security**: Password hashing using BCrypt.

---

## System Architecture

### Core Modules

1. **Authentication System (`LoginSignup`)**
   - User registration and login
   - Role-based access control
   - Password encryption with BCrypt
   - MySQL integration for user management

2. **Customer Interface (`CombinedPage`, `HomePage`, `EditPage`)**
   - Browse and select food items
   - Enter delivery details
   - Edit or update existing orders
   - View order history

3. **Scheduler Interface (`SchedulerPage`, `ScheduleOrderPage`)**
   - View all customer orders
   - Assign drivers to orders
   - Manage delivery scheduling
   - Monitor order status

4. **Driver Interface (`DriverHomePage`)**
   - View assigned delivery missions
   - Mark orders as delivered
   - Track delivery status

---

## Workflow

1. User registers or logs in with a specific role.
2. Customer places food orders with delivery details.
3. Scheduler assigns drivers to pending orders.
4. Driver receives missions and updates delivery status.
5. System tracks order completion.

---

## Database Structure

### Databases
- `userdb` — User accounts and orders
- `order_scheduler_db` — Scheduled orders and driver assignments

### Tables
- `users` — Stores user credentials and roles
- `final_order` — Stores all placed orders
- `scheduled_orders` — Stores driver assignments
- `mission` — Tracks delivery missions

---

## Technical Stack

- **Frontend:** Java Swing GUI
- **Backend:** JDBC with MySQL
- **Security:** BCrypt password hashing
- **Data Format:** JSON for complex food item storage

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/food-delivery-system.git
