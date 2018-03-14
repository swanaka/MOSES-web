### How to install
#### Requirements
- Java 8
- Eclipse (with STS(Spring Tool Suite) and Buildship)  
(Better)
- Git
- Gradle

#### Procedure
1. Clone the project repository([MOSES-web](https://github.com/swanaka/MOSES-web) + [ship-simulation](https://github.com/swanaka/ship-simulation))
```
  git clone [URL]
```
2. Run gradle command like the following to set them up as eclipse projects.  
Windows
```
gradlew.bat eclipse
```  
Unix
```
./gradlew eclipse
```
3. Import the projects into Eclipse.  
  1. [file] -> [Import] -> [Git] -> [Projects from Git] -> [Existing local repository]
  2. Add your local repository.
  3. [Import existing Eclipse projects]
  4. Select All and finish.

4. Configure build path of Moses-web.
  - Right click "MOSES-web" on Eclipse and select [Build Path]
  - Add ship-simulation as [Projects].

If you succeed it, you can run the project "MOSES-web" as Spring boot app.
Please try it on eclipse.
