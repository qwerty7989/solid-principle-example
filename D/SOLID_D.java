package D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// ? Low-level
class Person {
    public String name;

    public Person(String name) {
        this.name = name;
    }

}

enum Relationship {
    PARENT, CHILD, SIBLING
}

class Triplet<T, U, V> {
    private final T first;
    private final U second;
    private final V third;

    public Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirstValue() {
        return first;
    }

    public U getSecondValue() {
        return second;
    }

    public V getThirdValue() {
        return third;
    }
}

class Relationships {
    private List<Triplet<Person, Relationship, Person>> relation = new ArrayList<>();

    public List<Triplet<Person, Relationship, Person>> getRelation() {
        return relation;
    }

    public void addParentAndChild(Person parent, Person child) {
        relation.add(new Triplet(parent, Relationship.PARENT, child));
        relation.add(new Triplet(child, Relationship.CHILD, parent));
    }
}

class Research {
    public Research(Relationships relationships) {
        // ? High-level: find all of john's children
        List<Triplet<Person, Relationship, Person>> relations = relationships.getRelation();

        relations.stream()
                .filter(x -> x.getFirstValue().name.equals("John") &&
                        x.getSecondValue() == Relationship.PARENT)
                .forEach(ch -> System.out.println("John has a child name " + ch.getThirdValue().name));
    }
}

// ? The right way, High-level
interface RelationshipBrowser {
    List<Person> findAllChildrenOf(String name);
}

class BetterRelationships implements RelationshipBrowser {
    private List<Triplet<Person, Relationship, Person>> relation = new ArrayList<>();

    public List<Triplet<Person, Relationship, Person>> getRelation() {
        return relation;
    }

    public void addParentAndChild(Person parent, Person child) {
        relation.add(new Triplet(parent, Relationship.PARENT, child));
        relation.add(new Triplet(child, Relationship.CHILD, parent));
    }

    @Override
    public List<Person> findAllChildrenOf(String name) {
        return relation.stream().filter(x -> x.getFirstValue().name.equals(name) &&
                x.getSecondValue() == Relationship.PARENT)
                .map(Triplet::getThirdValue)
                .collect(Collectors.toList());
    }
}

class BetterResearch {
    public BetterResearch(RelationshipBrowser browser) {
        List<Person> children = browser.findAllChildrenOf("John");

        for (Person person : children) {
            System.out.println("John has a child name " + person.name);
        }
    }
}

public class SOLID_D {
    public static void main(String[] args) {
        Person parent = new Person("John");
        Person child1 = new Person("Mike");
        Person child2 = new Person("Abel");

        // ? Low-level module
        Relationships relationships = new Relationships();
        relationships.addParentAndChild(parent, child1);
        relationships.addParentAndChild(parent, child2);

        new Research(relationships);

        BetterRelationships betterRelationships = new BetterRelationships();
        betterRelationships.addParentAndChild(parent, child1);
        betterRelationships.addParentAndChild(parent, child2);

        new BetterResearch(betterRelationships);
    }
}
