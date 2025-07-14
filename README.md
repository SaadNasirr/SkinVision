# SkinVision: AI-Powered Skin Disease Detection App

SkinVision is a mobile application that integrates a custom-trained Convolutional Neural Network (CNN) model to detect and classify **20 types of skin diseases** using computer vision and deep learning. It empowers users with **instant skin condition analysis** through an intuitive Android interface — combining healthcare accessibility with intelligent technology.

---

## Features

-  Detects 20 common skin conditions using a CNN
-  Android mobile application built with Kotlin
-  Custom-trained Keras model (.h5) using Kaggle dataset
-  Upload or capture skin images for instant predictions
-  Keeps the history of diagnosises
-  Gives Doctors details and Precautions with profile apdation and secure login
-  Firebase integration (secured, no exposed keys)
-  Beginner-friendly UI for public use and research

---

## Dataset

We used the [20 Skin Diseases Image Dataset](https://www.kaggle.com/datasets/haroonalam16/20-skin-diseases-dataset) from Kaggle, containing labeled images for 20 distinct conditions.
Although the dataset is still attached here in the 20_Skin_Diseases folder.

The dataset was split into training and validation sets. Image augmentation and resizing were performed to enhance generalization.

---

## Model Info

The CNN model was built using **Keras (TensorFlow backend)** with:
- Convolutional layers + ReLU
- MaxPooling
- Dropout
- Dense layers
- Softmax output for multi-class classification

The `.h5` model file was excluded from GitHub due to size limits. You can download it here:

**[Download skinvision_cnn.h5 (Google Drive)](https://drive.google.com/file/d/1VHDun3VazgGZxK2Y0fukiyLzB9HN3DvN/view?usp=drive_link)**

---

## Android App Structure

- **Kotlin**-based Android app with image selection, prediction output, and results display
- `MainActivity.kt` handles model input and UI
- Integration with **Firebase ML Kit** (optional features)
- Permissions for camera/gallery included
- Multiple Fragments for every page for all type of screens and better performance of the app

---

## How to Run This Project

### AI Model (Jupyter Notebook)
1. Open `Skin-disease-cnn.ipynb` in Jupyter or Google Colab
2. Download the dataset from Kaggle and place it in the specified directory
3. Train the model or load the pretrained `.h5` file
4. Run the cell to make predictions

### Android App (SkinVision_app)
1. Open project in **Android Studio**
2. Place the downloaded `skinvision_cnn.h5` in the `assets/` folder (if using local prediction)
3. As the h5 file has a large file size i used tflite model (converted the h5 file into tflite and placed it in assests/ folder in android studio)
4. Sync Gradle and build the app
5. Run the code Flask_API in Visual Studio Code or any platform (MAKE SURE THAT THE IP ADDRESS OF THE FLASK_API, Your computer and the Homefragment.kt has same ip address)
6. Once the Flask_API is running
7. Run on a device or emulator
8. Enjoy the AI predictions and the app

---

## Tech Stack

- Python, Keras, TensorFlow
- OpenCV, Matplotlib, NumPy
- Kotlin, Android SDK
- Firebase (Authentication + Storage)
- Google Colab / Jupyter Notebook

---

## Team Members

- **Saad Nasir** ([SaadNasirr](https://github.com/SaadNasirr))
- *Add your teammates here if needed...*

> Want your GitHub usernames linked here? Send them in and I’ll update this for you.

---

## License
This project is our Semester project from Junior Year 6th Semester that includes three main courses of Artificial Intelligence AI, Computer Vision CV and Mobile App development MAD
This project is for educational and research purposes only.

---

## Notes

- This app does **not replace professional medical advice**
- For accurate diagnosis, consult a dermatologist

---

## Links

- 🔗 [Kaggle Dataset](https://www.kaggle.com/datasets/haroonalam16/20-skin-diseases-dataset)
- 🔗 [Google Drive Model (.h5)](https://drive.google.com/file/d/1VHDun3VazgGZxK2Y0fukiyLzB9HN3DvN/view?usp=drive_link)
- 🔗 [GitHub Repo](https://github.com/SaadNasirr/SkinVision)

---

### Thank you for visiting this project. Feel free to star the repo and give feedback! For further queries you can always contact me at saad.nasirr.23@gmail.com
