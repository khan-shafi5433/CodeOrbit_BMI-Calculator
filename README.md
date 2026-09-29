# CodeOrbit – BMI Calculator 🧮

A modern Android BMI Calculator built with **Kotlin and Android Studio**, designed to provide accurate BMI calculations through a clean, responsive, and user-friendly interface.

The application supports multiple measurement units, BMI classification, visual BMI indication, calculation history, input validation, and light/dark themes.

---
## 📸 Screenshots

| Calculator | BMI Result |
<img width="704" height="1600" alt="image_2026-09-29_220638774" src="https://github.com/user-attachments/assets/575788bb-7f1a-47db-8b51-a83e9b633586" />
<img width="540" height="1530" alt="image_2026-09-29_220631952" src="https://github.com/user-attachments/assets/82e05c25-9d5d-4284-9b03-e532b7415c14" />


## 📱 Features

* 📏 **Multiple Height Units**

  * Centimeters (cm)
  * Meters (m)
  * Feet & Inches (ft/in)

* ⚖️ **Multiple Weight Units**

  * Kilograms (kg)
  * Pounds (lb)

* 🧮 **Accurate BMI Calculation**

  * Automatic unit conversion
  * Standard BMI calculation

* 📊 **BMI Classification**

  * Underweight
  * Normal
  * Overweight
  * Obese

* 📈 **Visual BMI Range Indicator**

  * Provides a quick visual representation of the calculated BMI

* 📝 **BMI History**

  * Automatically saves previous calculations
  * Displays BMI, category, measurements, and date
  * Clear history option

* 🌙 **Light & Dark Mode**

  * Switch between light and dark themes
  * Theme preference is saved

* ✅ **Input Validation**

  * Detects empty and invalid inputs
  * Validates height and weight ranges
  * Provides clear error messages

* 📱 **Responsive UI**

  * Clean interface designed for different Android screen sizes

---

## 🛠️ Tech Stack

* **Kotlin** – Application development
* **Android Studio** – Development environment
* **Android SDK** – Android platform development
* **XML** – UI design
* **Gradle** – Build system
* **SharedPreferences** – Local history and settings storage

---

## 🧮 BMI Calculation

BMI is calculated using:

**BMI = Weight (kg) / Height² (m)**

The application automatically converts supported units into kilograms and meters before calculating BMI.

### BMI Categories

|           BMI | Category    |
| ------------: | ----------- |
|    Below 18.5 | Underweight |
|   18.5 – 24.9 | Normal      |
|   25.0 – 29.9 | Overweight  |
| 30.0 or above | Obese       |

> BMI is a general screening measurement and does not by itself provide a medical diagnosis.

---

## 🚀 Getting Started

### Prerequisites

* Android Studio
* Android SDK
* Compatible JDK
* Android device or emulator

### Installation

Clone the repository:

```bash
git clone https://github.com/khan-shafi5433/CodeOrbit_BMI-Calculator.git
```

Open the project in **Android Studio**, allow Gradle to sync, connect an Android device or start an emulator, and click **Run ▶**.

---

## 📂 Project Structure

```text
CodeOrbit_BMI-Calculator/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/bmicalculator/
│           │       └── MainActivity.kt
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   │   └── activity_main.xml
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

---

## 🔐 Data & Privacy

BMI calculation history and application preferences are stored **locally on the device**.

No account or cloud service is required to calculate BMI.

---

## 🎯 Project Goals

This project was developed to strengthen practical skills in:

* Kotlin programming
* Android application development
* UI/UX implementation
* Unit conversion
* Input validation
* Local data persistence
* Theme management
* Application testing
* Git and GitHub

---

## 🔮 Future Improvements

* 📊 BMI trend charts
* 📅 Advanced history filtering
* 📤 Export BMI history
* 🔔 Health reminders
* 🎨 Additional UI customization
* 📱 Tablet optimization

---

## 👨‍💻 Developer

**Mohd Shafi**

BTech Computer Engineering
AIKTC Engineering, Panvel

**Technologies:**
`Kotlin` `Android` `Android Studio` `XML` `Gradle` `Git` `GitHub`

---

## 📄 License

This project is available for educational and portfolio purposes.
