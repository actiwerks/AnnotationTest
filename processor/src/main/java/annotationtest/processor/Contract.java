package annotationtest.processor;

public @interface Contract {
    String value() default "CatchAllContracts";
}
