Windows-Contact-To-Vcard-Converter
==================================

Windows contact (.contact) to VCard (.vcf) converter

## What is it?

It's a simple tool to convert a Windows contact format, it has the '.contact' extension to VCard format,
which has the '.vcf' extension


## How It Works?

Currently on works in a command line, no graphical user interface yet :)


## How To Use?

Run this on command line:

        java -jar contacts2vcf.jar [Options] [path_of_windows_contact] [Target_dir]

Where:


+ [Options]: This is mandatory, and if exists here's the options:
    <code>-f : To indicate that the second argument is a file</code>
+ [Path]: file path or folder path
+ [Target_dir]: target directory (default current execution path)

For example:

        java -jar contacts2vcf.jar backup_contact vcf_converted



## What are the requirements?

This tools needs Java Runtime, at least 1.6 to run. You can download it here http://java.com/en/download/index.jsp





PhynxSoft 2013


