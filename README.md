<img alt="banner" src="https://raw.github.com/alibagherifam/kavoshgar/master/banner.png">

Kavoshgar is a Simple Service Discovery Protocol (SSDP) written in Kotlin.

As a demo, I also developed a LAN messenger on top of it.

## SSDP

According to [Wikipedia](https://en.wikipedia.org/wiki/Simple_Service_Discovery_Protocol):

> "The Simple Service Discovery Protocol (SSDP) is a network protocol based on the 
> Internet protocol suite for advertisement and discovery of network services and 
> presence information. It accomplishes this without assistance of server-based 
> configuration mechanisms, such as Dynamic Host Configuration Protocol (DHCP) or 
> Domain Name System (DNS), and without special static configuration of a network host."

## Usage

Every node on the network (which is a client by default) should start discovering
available servers:

```kotlin
    val client = LobbyDiscoveryRepository()
    service.startDiscovery()
```

You can get discovered servers as follow:

```kotlin
    client.getDiscoveredLobbies().collect { server ->
        println("Discovered a server:${server.address}")
    }
```

On the other hand, only the nodes you want to be a server in the network should 
be prepared to replying to client discoveries:

```kotlin
    val server = DiscoveryReplyRepository(name = "Test Server")
    server.startDiscoveryReplying()
```

## ▶ Demo

Kavoshgar comes with a demo application. It is a LAN messenger that you can chat with 
anybody on the same network. The application is built on top of Kavoshgar to showcase 
its usage and features. Currently, only Android version of the messenger is available, but 
I am developing a desktop version using Kotlin multiplatform. So stay tuned ;)

## ⚙ Technologies

- Socket Programming
- Kotlin Coroutines
- Jetpack Compose
- AndroidX Lifecycle
- Version Catalog
- Coil

## 💡 Inspiration

The subject was the final project for the Internet Engineering course during my 
B.Sc. in Computer Engineering.

## 🤝 Contribution

Feel free to create pull requests.

## 🙏 Acknowledgment

Thanks to [Dr. Nastooh Taheri Javan](https://scholar.google.com/citations?user=PmjCrgMAAAAJ&hl=en) for his guidance toward the project.

License
-------

	Copyright (C) 2023 Ali Bagherifam

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
