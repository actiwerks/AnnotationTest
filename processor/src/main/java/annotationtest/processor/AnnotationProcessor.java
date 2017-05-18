package annotationtest.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("annotationtest.processor.Contract")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Messager messager = processingEnv.getMessager();
        for (TypeElement element : annotations) {
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Processing : " + element + " of kind : " + element.getKind() + " enclosed : " + element.getEnclosingElement());
        }

        HashSet<String> nameEntries = new HashSet<String>();
        HashMap<String, HashSet<String>> entriesMap = new HashMap<String, HashSet<String>>();

        // for each javax.lang.model.element.Element annotated with the Contract
        for (Element element : roundEnv.getElementsAnnotatedWith(Contract.class)) {
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Found : " + element + " of kind : " + element.getKind() + " enclosed : " + element.getEnclosingElement());
            String objectType = element.getSimpleName().toString();
            Contract contractAnnotation = element.getAnnotation(Contract.class);
            HashSet<String> entry = entriesMap.get(contractAnnotation.value());
            if (entry == null) {
                entry = new HashSet<String>();
            }
            entry.add(objectType);
            entriesMap.put(contractAnnotation.value(), entry);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(ContractArray.class)) {
            String objectType = element.getSimpleName().toString();
            ContractArray contractAnnotationArray = element.getAnnotation(ContractArray.class);
            Contract[] annotationArray = contractAnnotationArray.value();
            for (Contract contractAnnotation : annotationArray) {
                HashSet<String> entry = entriesMap.get(contractAnnotation.value());
                if (entry == null) {
                    entry = new HashSet<String>();
                }
                entry.add(objectType);
                entriesMap.put(contractAnnotation.value(), entry);
            }
        }

        Iterator<String> entryNameIterator = entriesMap.keySet().iterator();
        while (entryNameIterator.hasNext()) {
            String entryName = entryNameIterator.next();
            HashSet<String> entryHashSet = entriesMap.get(entryName);
            StringBuilder entryBuilder = createBuilderWithHeader(entryName);
            writeMethods(entryBuilder, entryHashSet);
            writeFooter(entryBuilder);
            writeContractFile(entryBuilder, entryName);
        }
        return true;
    }

    private StringBuilder createBuilderWithHeader(String entryName) {
        StringBuilder entryBuilder = new StringBuilder()
                .append("package annotationtest.processor.generated;\n\n")
                .append("public interface ") // open class
                .append("Contract").append(entryName)
                .append(" {\n\n");
        return entryBuilder;
    }

    private void writeFooter(StringBuilder entryBuilder) {
        entryBuilder.append("\n}\n\n");
    }

    private void writeMethods(StringBuilder entryBuilder, HashSet<String> entryHashSet) {
        Iterator<String> contractEntriesIterator = entryHashSet.iterator();
        while (contractEntriesIterator.hasNext()) {
            String contractEntryName = contractEntriesIterator.next();
            entryBuilder.append("\tpublic void ") // open method
                    .append(contractEntryName)
                    .append("();\n"); // close method
        }
    }

    private void writeContractFile(StringBuilder entryBuilder, String entryName) {
        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile("annotationtest.processor.generated.Contract"+entryName);


            Writer writer = source.openWriter();
            writer.write(entryBuilder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
    }
}
