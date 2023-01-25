package I;

import java.io.NotActiveException;

class Document {

}

interface Machine {
    void print(Document d);

    void fax(Document d) throws Exception;

    void scan(Document d) throws Exception;
}

class MultiFunctionPrinter implements Machine {

    @Override
    public void print(Document d) {

    }

    @Override
    public void fax(Document d) {

    }

    @Override
    public void scan(Document d) {

    }

}

class OldSchoolPrinter implements Machine {

    @Override
    public void print(Document d) {

    }

    @Override
    public void fax(Document d) throws NotActiveException {
        throw new NotActiveException();
    }

    @Override
    public void scan(Document d) throws NotActiveException {
        throw new NotActiveException();
    }
}

interface Printer {
    void print(Document d);
}

interface Scanner {
    void scan(Document d);
}

interface Fax {
    void fax(Document d);
}

class GoodPrinter implements Printer, Scanner, Fax {

    @Override
    public void print(Document d) {

    }

    @Override
    public void scan(Document d) {

    }

    @Override
    public void fax(Document d) {

    }
}

class JustAPrinter implements Printer {
    @Override
    public void print(Document d) {
    }
}

class Photocopier implements Printer, Scanner {
    @Override
    public void print(Document d) {
    }

    @Override
    public void scan(Document d) {
    }
}

class SOLID_I {

}
