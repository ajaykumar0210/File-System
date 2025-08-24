import java.util.*;
class File{
    String name;
    StringBuilder content;
    List<String> versions;
    Directory parentDirectory;
    File(String name, String content, Directory parentDirectory){
        this.name = name;
        this.content = new StringBuilder(content);
        this.versions = new ArrayList<>();
        this.versions.add(content);
        this.parentDirectory = parentDirectory;
    }
    void updateContent(String newContent){
        content = new StringBuilder(newContent);
        versions.add(newContent);
    }

    void revertToVersion(int versionNumber){
        if(versionNumber >=0 && versionNumber < versions.size()){
            content = new StringBuilder(versions.get(versionNumber));
            versions.add(versions.get(versionNumber));
        }
        else{
            System.out.println("Invalid version number");
        }
    }
}
class Directory{
    String name;
    Directory parent;
    Map<String, File> files;
    Map<String, Directory> subDirs;

    Directory(String name, Directory parent){
        this.name = name;
        this.parent = parent;
        this.files = new HashMap<>();
        this.subDirs = new HashMap<>();
    }

    Directory getOrCreateSubDir(String dirName){
        subDirs.putIfAbsent(dirName, new Directory(dirName, this));
        return subDirs.get(dirName);
    }

    File getFile(String fileName){
        return files.get(fileName);
    }

    void  addFile(File file){
        files.put(file.name, file);
    }

    void removeFile(String fileName){
        files.remove(fileName);
    }
}

class FileSystem {
    Directory root;
    FileSystem() {
        root = new Directory("/", null);
    }

    private Directory traversePath(String path){
        String[] parts = path.split("/");
        Directory curr = root;
        for(int i = 0; i<parts.length-1; i++){
            if(!parts[i].isEmpty()){
                curr = curr.getOrCreateSubDir(parts[i]);
            }
        }
        return curr;
    }

    void createFile(String path, String content){
        Directory dir = traversePath(path);
        String fileName = path.substring(path.lastIndexOf('/') +1);
        File file = new File(fileName, content, dir);
        dir.addFile(file);
        System.out.println("File created: " + path);
    }

    void deleteFile(String path){
        Directory dir = traversePath(path);
        String fileName = path.substring(path.lastIndexOf('/') +1);
        dir.removeFile(fileName);
        System.out.println("File deleted: " + path);
    }
    void copyFile(String sourcePath, String destPath){
        Directory srcDir = traversePath(sourcePath);
        Directory destDir = traversePath(destPath);
        String srcFileName = sourcePath.substring(sourcePath.lastIndexOf('/')+1);
        File srcFile = srcDir.getFile(srcFileName);
        if(srcFile != null){
            File copyFile = new File(srcFile.name, srcFile.content.toString(), destDir);
            copyFile.versions = new ArrayList<>(srcFile.versions);
            destDir.addFile(copyFile);
            System.out.println("File copied to:" + destPath +"/" + copyFile.name);
        }
    }
    void moveFile(String sourcePath, String destPath){
        Directory srcDir = traversePath(sourcePath);
        Directory destDir = traversePath(destPath);
        String fileName = sourcePath.substring(sourcePath.lastIndexOf('/')+1);
        File file = srcDir.getFile(fileName);
        if(file != null){
            srcDir.removeFile(fileName);
            file.parentDirectory = destDir;
            destDir.addFile(file);
            System.out.println("File moved to:" + destPath);
        }
    }
    void revertFile(String path, int versionNumber){
        Directory dir = traversePath(path);
        String fileName = path.substring(path.lastIndexOf('/')+1);
        File file = dir.getFile(fileName);
        if(file != null){
            file.revertToVersion(versionNumber);
            System.out.println("File reverted to version:"+ versionNumber);
        }
    }

    void showFileContent(String path){
        Directory dir = traversePath(path);
        String fileName = path.substring(path.lastIndexOf('/')+1);
        File file = dir.getFile(fileName);
        if(file != null){
            System.out.println("Content of "+ path + ":" + file.content.toString());
        }
    }
}
public class VersionedFileSystem {
    public static void main(String args[]){
        FileSystem fs = new FileSystem();
        fs.createFile("/home/user/file1.txt", "Hello World");
        fs.showFileContent("/home/user/file1.txt");

        fs.copyFile("/home/user/file1.txt", "/home/user/docs");
        fs.showFileContent("/home/user/docs/file1.txt");

        fs.moveFile("/home/user/file1.txt", "/home/user/docs");
        fs.showFileContent("/home/user/docs/file1.txt");

        fs.revertFile("/home/user/docs/file1.txt", 0);
        fs.showFileContent("/home/user/docs/file1.txt");

    }
}
