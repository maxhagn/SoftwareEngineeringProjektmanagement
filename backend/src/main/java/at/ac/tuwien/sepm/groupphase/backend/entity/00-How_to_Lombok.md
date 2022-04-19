How to install lombok
--
- If you have IntelliJ(2019.3)
- MAC
   - Goto IntelliJ IDEA
       - Preferences
          - Plugins -> Search for Lombok (install) -> Restart IDE
       - Preferences
         - Other Tools
           - Lombok Plugin -> Enable lombok warning (2. von unten) -> Apply
   - Go to Maven -> Reimport
       - Terminal ./mvnw clean install
      - run ./mvnw clean compile spring-boot:run


- Windows
   - Goto IntelliJ IDEA
       - Settings
          - Plugins -> Search for Lombok (install) -> Restart IDE
       - Settings
         - Other Tools
           - Lombok Plugin -> Enable lombok warning (2. von unten) -> Apply
   - Go to Maven -> Reimport
       - Terminal ./mvnw clean install
      - run ./mvnw clean compile spring-boot:run

- If you have the newest version of IntelliJ (2020.1.x)
- MAC
    - Open to IntelliJ
    - Goto IntelliJ IDEA
       - Preferences
          - Plugins -> Zahnrad
             - Install plugin from disk (00-lombok.zip) -> Install -> Restart IDE
       - Check if plugin needs update
          -  Preferences -> Plugins -> Installed plugins
      - Go to Maven -> Reimport
      -Terminal ./mvnw clean install
          - run ./mvnw clean compile spring-boot:run
- Windows
    - Open to IntelliJ
    - Goto IntelliJ IDEA
       - Settings
          - Plugins -> Zahnrad
             - Install plugin from disk (00-lombok.zip) -> Install -> Restart IDE
       - Check if plugin needs update
          -  Settings -> Plugins -> Installed plugins
      - Go to Maven -> Reimport
      -Terminal ./mvnw clean install
          - run ./mvnw clean compile spring-boot:run

- Error that will occur when Lombok is added
  - Cannot find getAdmin : TODO -> Change to isAdmin()

How to to lombok
--
- See /entity/00-README.md