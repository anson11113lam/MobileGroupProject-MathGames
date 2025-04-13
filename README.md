# Math Games Integration Package

This repository contains three math games for integration into the MobileGroupProject Android application.

## Contents

- `MathGameActivity.java` - Basic math calculation game
- `MathGame2Activity.java` - Math game with draggable button mechanics
- `MathGame3Activity.java` - Math game with orientation change mechanics

## Integration Instructions

1. Copy the Java files to your project's `/app/src/main/java/com/yourpackage/games/` directory
2. Copy the layout XML files to your project's `/app/src/main/res/layout/` directory
3. Copy the landscape layout for MathGame3 to `/app/src/main/res/layout-land/`
4. Add the activities to your AndroidManifest.xml
5. Update the package declaration in each file if needed

## How to Launch the Games

Use the following code to launch each game from your activities:

```java
// For MathGameActivity
Intent intent = new Intent(YourActivity.this, MathGameActivity.class);
intent.putExtra("difficulty", 1); // 1-4 based on difficulty
startActivity(intent);

// For MathGame2Activity
Intent intent = new Intent(YourActivity.this, MathGame2Activity.class);
intent.putExtra("difficulty", 2);
startActivity(intent);

// For MathGame3Activity
Intent intent = new Intent(YourActivity.this, MathGame3Activity.class);
intent.putExtra("difficulty", 3);
startActivity(intent);
```