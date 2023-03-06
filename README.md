# Solid Principle Example in Java
This is example of SOLID principle from the lab of "Design pattern in Object-Oriented Programming" class

## What is SOLID?
It is set of five design principles to make software design more understandable, so other can read it and understand it right way. Flexible, to be able to change due to requirements. And maintainable, easily to be change or need to be change as little as it should. This is what forms the core philosphy for agile development and adaptive development.

- S - *Single responsibility*
- O - *Open - closed*
- L - *Liskov Substitution*
- I - *Interface segregation*
- D *- Dependency inversion*

### S | **Single Responsibility**
Let's say we have a Journal class and ithas structured like this,
```Java
class Journal
{
	private ArrayList<String> entries = new ArrayList<>();
	private int count = 0;

	public void addEntry(String text)
	{
		count++;
		entries.add(count + ": " + text);
	}

	public void removeEntry(int index)
	{
		count++;
		entries.remove(index);
	}

	@Override
	public String toString()
	{
		return String.join(System.lineSeparator(), entries);
	}
}
```
Okay, now we can add and remove the journal entry. But what about save these journal into file. Easy! We just add the `saveToFile` method into the `Journal` class. Like this.
```Java
class Journal
{
	public void addEntry(String text)
	{ ... }

	public void removeEntry(int index)
	{ ... }

	@Override
	public String toString()
	{ ... }

	public void saveToFiles(String filename, boolean overwrite) throws FileNotFoundException
	{
		if (overwrite || new File(filename).exists())
		{
			try (PrintStream out = new PrintStream(filename))
			{
				out.println(toString());
            }
		}
	}
}
```
Okay, but if we have something like newspaper. No problemo, just add another class `newspaper`. Then also add the `saveToFile` method for it too. But what about adding more? If we have like newspaper, journals, books, and a lot of other products. We would have a hard time writing off the `saveToFile` method. This isn't practical.

That's why we need SOLID, what we were doing was violate the S-principle of SOLID, [[#Single Responsibility]]. Because now the Journal not only have its own job as `addEntry` and `removeEntry`. It has to `saveToFile` for somehow.

The solution is to remove these `saveToFile` method and add it into the new class. Let's called it `Persistence` that responsible for saving these stuff into file. Like this
```Java
class Journal
{
	public void addEntry(String text)
	{ ... }

	public void removeEntry(int index)
	{ ... }

	@Override
	public String toString()
	{ ... }
}

class Persistence
{
	public void saveToFile (Journal journal,
							String filename,
							boolean overwrite) throws FileNotFoundException
	{
		if (overwrite || new File(filename).exists())
		{
			try (PrintStream out = new PrintStream(filename))
			{
				out.println(journal.toString());
			}
		}
	}
}
```
Now, we can use these classes to start working around. Let's look at the SOLID_S or the driver class below.
```Java
class SOLID_S
{
	public static void main(String[] args) throws Exception {
		Journal j = new Journal();
		j.addEntry("Hell World");
		j.addEntry("The world is fire");
		System.out.println(j);

		Persistence p = new Persistence();
		String filename = "journal.txt";
		p.saveToFile(j, filename, true);

		Runtime.getRuntime().exec("notepad.exe" + filename);
	}
}
```
This is how [[#Single Responsibility]] really meant to be. Each class has its own job to do and not take none other than its own job. If has to, just leave it to other.

### O | **Open - Closed**
Classes should be closed for modfication, while open for extension. As it may cause impact on other classes. And need to be re-analyzed to made sure the software is completed.

Let's say we're having some products. And the user want to search it with the filter. Of course, we'd need the products class. And filter class. But what's filter do we have? Color? Size? Type?

The answer is all of them. Let's try implement it in some way first.

Let's define some ENUM first. Enum works as some kind of concrete data variable. More like the constant value but more fixed. Imagine the concreted truth that can't be altered or changed. That's what enum is.
```Java
enum Color {
	RED, GREEN, BLUE
}

enum Size {
	SMALL, MEDIUM, LARGE
}
```

Now, let's create the product class.
```Java
class Product
{
	public String name;
	public Color color;
	public Size size;

    public Product(String name, Color color, Size size) {
		this.name = name;
		this.color = color;
		this.size = size;
    }
}
```
This is our regular product class, have the constructor that set the states of object.
Now we knew that our products have these type of Colors and Sizes as defined in Enum. Then what about the filter class to do the filter jobs. Of course, here it is.
```Java
class ProductFilter
{
	public Stream<Product> filterByColor(List<Product> products, Color color)
	{
		return products.stream().filter(p -> p.color == color);
	}

	public Stream<Product> filterBySize(List<Product> products, Size size)
	{
		return products.stream().filter(p -> p.size == size);
	}
}
```
As you can see. The `ProductFilter` class has `filterByColor` and `filterBySize` methods. This is what we gonna use to filter the product that contain these criteria. But what if we needs both the Colors and Sizes criteria. No problemo, just write another methods. Like example below:
```Java
class ProductFilter
{
	public Stream<Product> filterByColor(List<Product> products, Color color)
	{ ... }

	public Stream<Product> filterBySize(List<Product> products, Size size)
	{ ... }

    public Stream<Product> filterByColorAndSize(List<Product> products, Color color, Size size)
    {
        return products.stream().filter(p -> (p.color == color && p.size == size));
    }
}
```
As we combined these two filters together. Yeah, it works at small numbers. But what if we add even more filter, types of product, prices, soruce, and many other filter you'd imagine.

Now this violate the O-principle of SOLID, and you should stop doing it. The reason why this happen is because the flaw in our design as we values or plan to use the modification over the extension. This led to this filter problem to occurs.

The solution is to find a better approach to design this. First, we'll use an interface to check it anyclass satisfied the given conditions.
```Java
interface Specification<T> {
	boolean isSatisfied(T item);
}
```
Any classes that implements from this interface are required to have this method at any costs. But only interface alone won't doing much of its own, let's write our implemented class.
```Java
class ColorSpecification implements Specification<Product> {
	private Color color;

	public ColorSpecification(Color color) {
		this.color = color;
	}

	@Override
	public boolean isSatisfied(Product item) {
		return item.color == color;
	}
}
```
Now, the `filterByColor` we knew are now `ColorSpecification` which also have type/template for defined the type of item to check criteria. But that's not the important part.

The facts is, now these specification classes like `ColorSpecification` only has one jobs, to check the defined-specification. Color checks color, Size check size. That's it. Let's look at how `SizeSpecification` are written out:
```Java
class SizeSpecification implements Specification<Product> {
	private Size size;

	public SizeSpecification(Size size) {
		this.size = size;
	}

	@Override
	public boolean isSatisfied(Product item) {
		return item.size == size;
	}
}
```
Simple right? As simple as it should. And it look pretty clean and easy to understand right away of what this existed for. What it'll do and what its responsibility.

What's difference from previous filter is, the filter is not part of specification anymore. Each specification and be used in filter process. But need not to bound with it.

Then let's us create the filter class. But first, we'd need an interface for filter class too.
```Java
interface Filter<T> {
	Stream<T> filter(List<T> items, Specification<T> spec);
}
```
Why use the interface? Well, because it give the ability for filter to adapt and change for any incoming type of input. Let's say, the requirement changes from wanting not only the Product, but also the Reports.

What you gonna do? If we create a new Filter solely for Reports. That's feel a bit deja vu, right? Of course, it'll. Because we're repeated the same flaws like previous filter. If we just continiuosly add more and more of filter. It's gonna full of filter-this/that class.

That's why we use interface. If the incoming type of input changes. No problem, just change the type right away. Zero-stroke of keyboard needs to change the filter class.

Okay, let's stop with the blue. Now the filter class will look like this:
```Java
class BetterFilter<T> implements Filter<T> {
	@Override
	public Stream<T> filter(List<T> items, Specification<T> spec) {
		return items.stream().filter(spec::isSatisfied);
	}
}
```
See how beautiful it is? Even if the incoming input change. Feared not, my friends. This class will work like a charm. Now, what's left is the double criteria. Colors and Sizes. So, let's see how it was written:
```Java
class AndSpecification<T> implements Specification<T> {
    private Specification<T> first;
    private Specification<T> second;

    public AndSpecification(Specification<T> first, Specification<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfied(T item) {
        return first.isSatisfied(item) && second.isSatisfied(item);
    }
}
```
It may look a bit weird at first glance. But focused on the `isSatisfied` method. As it return the boolean like the same. But the condition inside the method are change. The other part is that this are the same type of implements class, Specification. Using the same interface as other but can perform double criteria.

This is what and why these SOLID principle are so powerful. It give us more flexibility in our design. An ability to adapt and change without re-written the entire software. That's what SOLID give us.

### L | **Liskov Substitution**
A base type should be able to be substituted by a subtype without altering the correctness of the program. This means the base class should be the main and its core never substituited.

For example, the humankind class should able to become Homosapien, Neanderthals, and Denisovans. Out of these 3 remains the base class of human like, ability to eat, ability to excrete, et cetera.

But never for these human to eat and excrete on the reverse. That would not be a human anymore. And that's would be really scary...

Anyway, let's get into the code by starting with our simple  `Rectangle` class.
```Java
class Rectangle {
    protected int width, height;

    public Rectangle() {}

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

	// ? Getter
    public int getWidth() { return width; }
    public int getHeight() { return height; }

	// ? Setter
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    public int getArea() { return width * height; }
}
```
This class has everything a normal rectangle would has, right? Now what if we want a `Square` class to specially for our little `Square`. We can write it out like the example below:
```Java
class Square extends Rectangle {
    public Square() {}

    public Square(int side) {
        width = side;
        height = side;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        super.setHeight(width);
    }

    @Override
    public void setHeight(int height) {
        super.setWidth(height);
        super.setHeight(height);
    }
}
```
Okay, it's seem pretty solid and promising here. So, let's try it out and see how this played out. In the main drive we'll have `useIt` function as an example of how `client` would use these class while not knowing what's inside:
```Java
class SOLID_L {
    static void useIt(Rectangle r) {
        int width = r.getWidth();

        r.setHeight(10);
        System.out.println("Expected area = " + (width * 10) + ", got " + r.getArea());
    }

    public static void main(String[] args) {
        Rectangle rc = new Rectangle(2, 3);
        useIt(rc);

        Square sq = new Square();
        sq.setHeight(5);
        useIt(sq);
    }
}
```
The result of the `Rectangle` would works fine, but for the `Square` it would go off a bit. At first, we're use the `setHeight` to set the height of `Square`, which we're knew that it'll set both the `height` and `width` to 5.

But in the `useIt` function, it get the `width` and use the `setHeight` to set the height to 10. Which for `Square` class, it was set to 10 both the `height` and `width`.

You see the problems here? `useIt` was expected the regular `Rectangle`, not the `Square`. That's what the problem here. It violates the L-principle in the SOLID.

What's you trying to do is to substitute the core of `Rectangle` class. You're trying to overwrite the way `height` and `width` of the `Rectangle` actually work, by fixing `height` and `width` to the same value to make a `Square`. You've violated this L-princicple.

The way to fix this issue is fairly simple, we can use the `Factory` design pattern to fix this like the code below:
```Java
class RectangleFactory {
    public static Rectangle newRectangle(int width, int height) {
        return new Rectangle(width, height);
    }

    public static Rectangle newSquare(int side) {
        return new Rectangle(side, side);
    }
}
```
Now, rather than focusing on change what's going on inside the `Rectangle` class. You're just narrow it from the outside. And this help remain the core abiility of  `Rectangle`, by able to change its width and height indepedently.

Let's look at the main driver again and see how this played out:
```Java
class SOLID_L {
    static void useIt(Rectangle r) {
        int width = r.getWidth();

        r.setHeight(10);
        System.out.println("Expected area = " + (width * 10) + ", got " + r.getArea());
    }

    public static void main(String[] args) {
        Rectangle rc2 = RectangleFactory.newRectangle(2, 3);
        useIt(rc2);
        Rectangle sq2 = RectangleFactory.newSquare(5);
        useIt(sq2);
    }
}
```
The result is as we're expected. Even we're create it with the intention of having the `Square` in mind. But as the `Square` are based from `Rectangle`. Its `width` and `height` must not be fixed with eachother. Able to remain its ability like how `Rectangle` class does. It followed the L-principle of SOLID.

### I | **Interface Segregation**
- One interface should serve specific purposes, not several general purpose.
- If there are too much in an interface, seperate it.
- Remember YAGNI: You Aren't Going to Need It.
Imagine you have printer. Most printer have 3 basis ability: print, fax, and scan. We'll create the `Machine` interface as each printer have their own method of completed these 3 basis tasks. Like the one below:
```Java
class Document { ... }

interface Machine {
	void print(Document d);
	void fax(Document d);
	void scan(Document d);
}
```
Now, this interface said that every printer `implements` from this `interface` must have these 3 basis method. Let's create the `MultiFunctionPrinter`:
```Java
class MultiFunctionPrinter implements Machine {
	@Override
	public void print(Document d) {
		...
	}

	@Override
	public void fax(Document d) {
		...
	}
	@Override
	public void scan(Document d) {
		...
	}
}
```
Okay, that's goes well. But what if we have an old school printer that can't even use for fax or scan. Like this one:
```Java
class OldSchoolPrinter implements Machine {

	@Override
	public void print(Document d) {
		...
	}

	@Override
	public void fax(Document d) {
		???
	}
   
	@Override
	public void scan(Document d) {
		???
	}
}
```
But how we're gonna deal with the `fax` and `scan` method? As this `OldSchoolPrint` doesn't have any feature to `fax` or `scan`. The quick fix is to `throws Exception` like:
```Java
class OldSchoolPrinter implements Machine {

	@Override
	public void print(Document d) {
		...
	}

	@Override
	public void fax(Document d) throws NotImplementedException {
		throw new NotImplementedException();
	}
   
	@Override
	public void scan(Document d) throws NotImplementedException {
		throw new NotImplementedException();
	}
}
```
Now it's fixed, but not ideally for what we'd use for. This act violate the I-principle of SOLID, Interface Segregation. Because now the machine interface are serving 3 purposes at a row. And these 3 said purpose are not bound to always existed at one at a time.

Like the previous example, there's various type of machine that only does a few of what `machine` interface provided. Like `OldSchoolPrinter`, it cannot send `fax` or `scan` the documents. But it still can `print`.

Should we fix this by define each type of machine interface, No! We'll seperate the interface into 3 interface:
```Java
interface Printer {
	void print(Document d);
}

interface Scanner {
	void scan(Document d);
}

interface Fax {
	void fax(Document d);
}
```
Now each of these abilities seperated, we'll create the class by picking only the ability that said machine possess:
```Java
class JustAPrinter implements Printer {
	@Override
	public void print(Document d) { ... }
}

class Photocopier implements Printer, Scanner {
    @Override
    public void print(Document d) { ... }

	@Override
	public void scan(Document d) { ... }
}
```
As you see, now these machine has only the abilities they'd have. The photocopier does not need to have `fax`. Printer is just a printer. Problem solved!

This is similiar to [[Component Technology]] as it used the concept of composition to solve these problem. See [[Component-Based Software Development]] for more information.
```Java
class SOLID_I {
	public static void main(String[] args) {
		JustAPrinter thisIsPrinter = new JustAPrinter();
		Photocopier thisIsPhotocopier = new PHotocopier();
	}
}
```

### D - **Dependency Inversion**
A change in low-level should not affect the flow in the high-level. Imagine using an MySQL database and later change to PostgreSQL. It should not change what we did to see the data in the database.

Two rules of this principle are:
1. High-level modules should not depend upon low-level ones, both should depend upon abstractions.
2. Abstractions should not depend upon details, details should depend upon abstractions.
