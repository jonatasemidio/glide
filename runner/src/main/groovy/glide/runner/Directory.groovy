package glide.runner

import groovy.transform.ToString

@ToString
class Directory {

    @Delegate
    private File root
    private Map children = [:]

    Directory(File root) { this.root = root }
    Directory(String root) { this(new File(root)) }

    File asFile() { root }
    def getAt(Object key) { children[key] }
    def propertyMissing(String name) { children[name] }

    def methodMissing(String name, args) {
        final String fileName = args.first().toString()
        final File file = new File(root, fileName)

        children[name] = (args.length > 1 && args.last() instanceof Closure) ?
            build(file.path, args.last()) :
            file

        return this
    }

    static build(String root, closure) {
        def directory = new Directory(root)
        closure?.resolveStrategy = Closure.DELEGATE_FIRST
        closure?.delegate = directory
        closure?.call()
        directory
    }
}