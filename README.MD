Contract Annotation POC
=======================


#### **What is Contract Annotation POC**

Sample project demonstrating replacing the Contract interfaces which are tedious to write and their
implementations abuse the language feature (@Override annotation) for unrelated reason,
this turns the logic there, defines a dedicated annotation (@Contract), defines AnnotationProcessor
which at the build time analyzes all the usage of this Contract annotation and creates the contract
interface files from this information. This way we have cleaner marker annotation, and there is no need
to create/maintain the interface file by hand. As extra feature, it is easy to extend the AnnotationProcessor
to perform some additional check(s).


#### **How to use**

MainActivity has some dummy methods that get annotated with the Contract annotation. This annotation should
have a value, which is a String constant, that defines the contract. It is possible for a method to be
part of multiple contracts, using ContractArray annotation, see doubleAgent() method for example.
The generated contract interfaces are located at app/build/generated/source/apt/debug/

#### **How it works**

AnnotationProcessor does most of the work. SupportedAnnotationTypes annotation ties it to Contract annotation
and resources/META-INF.services/javax.annotation.processing.Processor points build system to the AnnotationProcessor
class. Everything is updated during the build process (see the gradle files for additional tasks) and
generated files are first class citizens, so they can even be used within the application.
The processing of annotated elements in the AnnotationProcessor is simplified (cheating to assume all methods
are returning void and have no parameters) but this would be easy to change and is sufficient for
the illustration purposed for this POC. Real implementation should use robust solution such as JavaPoet