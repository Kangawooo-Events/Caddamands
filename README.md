Dependancy:

````

repositories {
    ...
    maven {
        url = uri("https://maven.pkg.github.com/kangawooo-events/caddamands")
    }
    ...
}

...
dependencies {
    ...
    compileOnly("cd.arnett:caddamands:<Package-Version>")
    ...
}
````
