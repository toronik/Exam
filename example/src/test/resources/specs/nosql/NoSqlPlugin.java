import io.github.adven27.concordion.extensions.exam.nosql.NoSqlPlugin;

public class Specs extends AbstractSpecs {

    @Override
    protected ExamExtension init() {
        return new ExamExtension(
            new NoSqlPlugin(
                    new MongoTester("localhost:27017", "myDB")
            )
        );
    }
}