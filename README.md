# DroidGPT
DroidGPT is a custom OpenAI API client for Android that provides an easy to use chat interface built with Jetpack Compose.

## Purpose
I started this project for two main reasons:
- I wanted a ChatGPT app for android
- I wanted to explore Jetpack Compose and Kotlin for self improvement


Soon after I started developing the app, OpenAI announced its official app, but this did not make me stop the devloping: I had free time and I wanted to learn Jetpack Compose and the new standards of Android app developing. I also wanted to code a MaterialYou app with dynamic colors.
Before starting making this app, I had zero knowledge about Jetpack Compose and Material3 standards, in fact I started developing an early app using old standards and views using xml files: I was not happy about the result and it seemed too heavy for such a small app like this, in that
moment, while doing some web researches, I met Jetpack Compose and I tried it, then I dropped my first app and I switched to Compose, restarting from scratch.


## Features
### Material3 and MaterialYou 
This app is recommended for the latest Android releases (currently I am using Android 13). The app works on older devices but I did not refine the app for older models too, maybe in some future.

### Room database
Conversations are stored in database when cleared, they can be continued later.

### Custom API implementation (no more)
I started this project coding my own OpenAI's API implementation, but later i switched to the prebuilt openai-kotlin client by aallam.

### Change "System message"
System message defines the behaviour the chatbot should have throughout the conversation

### Change temperature
The temperature is the level of randomness and unpredictability of the chabot's replies: the higher the value, the more creative it'll be.

### Stream mode
You can choose to enable stream mode, where the completions arrive in chunks.


## Conclusion
This app still needs improvements, but it is perfectly functioning.

## Demo
https://github.com/LucianoBigliazzi/DroidGPT/assets/101213045/cb763f5b-abd4-4e9b-82e9-e0c4f714aa6b
