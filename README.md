# NFC Reader Lib

A simple Android NFC reader library designed for easy integration into your applications. This library handles the basic NFC tag reading process and provides callbacks for success and failure.

## Table of Contents

* [Features](#features)
* [Installation](#installation)
    * [Using JitPack (Recommended for GitHub)](#using-jitpack-recommended-for-github)
    * [Using GitLab Package Registry (Advanced)](#using-gitlab-package-registry-advanced)
* [Usage](#usage)
* [Callbacks](#callbacks)
* [Contributing](#contributing)
* [License](#license)

## Features

* Simple API for NFC tag reading.
* Callbacks for processing, success, and failure.
* Handles foreground dispatch for better NFC interaction.

## Installation

You can integrate this library into your Android project using either JitPack (recommended for GitHub repositories) or GitLab Package Registry (for advanced use cases like private repositories or strong CI/CD).

### Using JitPack (Recommended for GitHub)

JitPack allows you to directly use GitHub repositories as Gradle dependencies.

1.  **Add JitPack to your project's `settings.gradle.kts` (or `settings.gradle` for Groovy):**

    ```groovy
    // settings.gradle (Groovy)
    pluginManagement {
        repositories {
            google()
            mavenCentral()
            maven { url '[https://jitpack.io](https://jitpack.io)' } // ✅ Add this line
            gradlePluginPortal()
        }
    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url '[https://jitpack.io](https://jitpack.io)' } // ✅ Add this line
        }
    }
    ```

    Or for Kotlin DSL (`settings.gradle.kts`):

    ```kotlin
    // settings.gradle.kts (Kotlin)
    pluginManagement {
        repositories {
            google()
            mavenCentral()
            maven { url = uri("[https://jitpack.io](https://jitpack.io)") } // ✅ Add this line
            gradlePluginPortal()
        }
    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url = uri("[https://jitpack.io](https://jitpack.io)") } // ✅ Add this line
        }
    }
    ```

2.  **Add the dependency to your app's `build.gradle` (module level):**

    ```groovy
    // app/build.gradle (or build.gradle.kts for Kotlin)
    dependencies {
        implementation 'com.github.hoangduong536:nfc-reader-lib:1.0.0' // [cite: 1]
    }
    ```
    **Note:** Replace `1.0.0` with the latest stable version or `master-SNAPSHOT` for the latest development version if `1.0.0` is not yet stable/built successfully on JitPack. Check the JitPack page for the latest status: [https://jitpack.io/#hoangduong536/nfc-reader-lib](https://jitpack.io/#hoangduong536/nfc-reader-lib)

### Using GitLab Package Registry (Advanced)

This method is more complex but beneficial for private repositories or robust CI/CD pipelines[cite: 10].

1.  **Configure your module's `build.gradle` for publishing:** [cite: 4]

    Make sure your module's `build.gradle` (or `build.gradle.kts`) applies the `maven-publish` plugin and includes the `publishing` block as follows: [cite: 4]

    ```groovy
    // YourLibraryModule/build.gradle (Groovy)
    plugins {
        id 'com.android.library'
        id 'maven-publish' // Add this plugin [cite: 4]
        // ... other plugins
    }

    // ... android { ... } block

    publishing {
        publications {
            release(MavenPublication) {
                groupId = 'com.gitlab.duong-futa' // [cite: 4]
                artifactId = 'nfc-reader-lib' // [cite: 4]
                version = '1.0.0' // [cite: 4]
                afterEvaluate {
                    from components.release // [cite: 5]
                }
            }
        }
        repositories {
            maven {
                name = "GitLab"
                url = uri("[https://gitlab.com/api/v4/projects/](https://gitlab.com/api/v4/projects/)<PROJECT_ID>/packages/maven") // [cite: 5]
                credentials(HttpHeaderCredentials) {
                    name = "Private-Token" // [cite: 6]
                    value = gitlabToken // [cite: 6] (from environment variable or gradle.properties [cite: 7])
                }
                authentication {
                    header(HttpHeaderAuthentication)
                }
            }
        }
    }
    ```

2.  **Get `PROJECT_ID` and create a Personal Access Token:** [cite: 7]

    * **PROJECT_ID:** Go to your GitLab repository -> `Settings` > `General` > `Project ID`. [cite: 7]
    * **Personal Access Token:** Create one at `https://gitlab.com/-/profile/personal_access_tokens` with `read_api`, `write_repository`, `write_package_registry` permissions. [cite: 7]

3.  **Publish to GitLab:** [cite: 7]

    Run the Gradle task:
    ```bash
    ./gradlew publish
    ```

4.  **Use in another app:** [cite: 7]

    * **Add to `settings.gradle`:** [cite: 8]
        ```groovy
        // settings.gradle (Groovy)
        dependencyResolutionManagement {
            repositories {
                maven {
                    url = uri("[https://gitlab.com/api/v4/projects/](https://gitlab.com/api/v4/projects/)<PROJECT_ID>/packages/maven") // [cite: 8]
                    credentials(HttpHeaderCredentials) {
                        name = "Private-Token" // [cite: 8]
                        value = "<YOUR_GITLAB_PERSONAL_TOKEN>" // [cite: 8]
                    }
                    authentication {
                        header(HttpHeaderAuthentication)
                    }
                }
                google() // [cite: 9]
                mavenCentral() // [cite: 9]
            }
        }
        ```
    * **And in `build.gradle` (app module):** [cite: 9]
        ```groovy
        // app/build.gradle (Groovy)
        dependencies {
            implementation 'com.gitlab.duong-futa:nfc-reader-lib:1.0.0' // [cite: 9]
        }
        ```

## Usage

Here's how to integrate and use `NfcReader` in your `Activity`:

```kotlin
import android.nfc.NfcAdapter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hoangduong536.nfcreaderlib.NfcCallback
import com.hoangduong536.nfcreaderlib.NfcReader // Make sure this import matches your library's package structure

class MainActivity : AppCompatActivity(), NfcCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // [cite: 2]
        NfcReader.init(this, this) // [cite: 2]
    }

    override fun onResume() {
        super.onResume()
        NfcReader.enableForegroundDispatch(this) // [cite: 2]
    }

    override fun onPause() {
        super.onPause()
        NfcReader.disableForegroundDispatch(this) // [cite: 3]
    }

    override fun onProcessing() {
        Log.d("NFC", "Đang xử lý NFC...") // [cite: 3]
    }

    override fun onSuccess(tagData: String) {
        Log.d("NFC", "Đọc thành công: $tagData") // [cite: 3]
    }

    override fun onFailure(errorMessage: String) {
        Log.e("NFC", "Lỗi: $errorMessage") // [cite: 3]
    }
}
