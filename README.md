WORK IN PROGRESS, PLEASE COME BACK LATER

# Better SQL in Java

WARNING : this project is only a POC, don't use it for real use cases.

## The question

I'm not happy with current solutions for SQL ORM in Java : they all suck.

Is it a limitation of Java, or can we do better ?

## What I want

What I want is rely on Java paradigm :

- As much type safety as possible
- As much compile-time validation as possible
- If you can't validate something during compilation, do it during startup 
- Clean API
- No type cast
- No dark magic at runtime

## The idea

Describe your SQL model in XML and use this XML to generate Java code.

The Java code generation should enable to fine tune the query API for maximum safety.

With this grand plan, see how good is the API can be.

## Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<modelDescription>
    <model name="customer">
        <primaryKey name="id"/>
        <column name="name" type="String" nullable=true />
    </model>
    <model name="order">
        <primaryKey name="id"/>
        <column name="name" type="String" nullable=true />
    </model>
</modelDescription>
``