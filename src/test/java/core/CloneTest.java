package core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CloneTest {
    @Test
    public void testClone() {
        Name[] names = new Name[1];
        names[0] = new Name("peter");
        Name[] namesClone = names.clone();
        namesClone[0] = new Name("james");
        System.out.println(names[0].getName());
        System.out.println(namesClone[0].getName());
    }

    @Test
    public void testClone2() {
        ArrayList<Name> names = new ArrayList<Name>();
        names.add(new Name("peter"));

        ArrayList<Name> namesClone = (ArrayList<Name>) names.clone();
        names.get(0).name = "james";

        System.out.println(names.get(0).getName());
        System.out.println(namesClone.get(0).getName());
    }

    @Test
    public void testClone3() {
        Human[] humans = new Human[1];
        humans[0] = new Human(new Name("peter"));
        Human[] humansClone = humans.clone();
        humansClone[0].setName("james");

        System.out.println(humans[0].getName());
        System.out.println(humansClone[0].getName());

    }

    public class Name {
        Name(String name) {
            this.name = name;
        }
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Human {
        Name name;
        public Human(Name name) {
            this.name = name;
        }

        public String getName() {
            return this.name.getName();
        }

        public void setName(String name) {
            this.name.setName(name);
        }
    }
}
