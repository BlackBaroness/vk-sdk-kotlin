## VK SDK Kotlin

The only VK (https://vk.ru) SDK for JVM that’s actually usable.

- Built with Kotlin in mind (`ktor`, `kotlinx.serialization`)
- Minimal, fast, and efficient
- Non-blocking (consumes no threads if you use `CIO` as a Ktor HTTP client)
- Object-oriented: run multiple independent bots and shut them down cleanly, no hidden static mess

**Note:** the API coverage is far from 100%, but you can easily contribute by adding methods you need.

## Adding it to your project

```kotlin
implementation("io.github.blackbaroness:vk-sdk-kotlin:1.0.2")
```

## Example (echo bot)

```kotlin
// let's create a bot
val bot = VkClient("your token", CIO /* or any other ktor http client */)

// we should probably make sure group owner configured longpoll correctly
bot.groups.setLongPollSettings(groupId) {
    enabled = 1
    messageNew = 1
}

fun handleUpdate(update: Update) {
    // cast update to "message new" if possible, otherwise just ignore
    update.asMessageNew?.also { update ->
        // just send the copy of text as a reply to the message received
        bot.messages.send(update.message.fromId) {
            replyTo = update.message.id
            message = update.message.text
        }
    }
}

// now we are ready to poll updates
// since .collect will suspend us, we better start it on other coroutine
val pollJob = scope.launch(Dispatchers.Default) {
    bot.startLongPolling(groupId, logger).collect { update ->
        try {
            handleUpdate(update)
        } catch (e: Throwable) {
            println("Error handling VK update '$update'", e)
        }
    }
    println("Stopped polling VK updates")
}

// the bot is polling now!
// let's stop it after 1 minute
delay(1.minutes)
pollJob.cancel() // stop polling
bot.cancel() // close http client
```

There is much more methods available in that library, go check it out!
