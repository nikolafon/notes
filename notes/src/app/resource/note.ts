export class Note {
    id: string | undefined;
    title: string;
    content: string;
    collaborators: string[];
    constructor(title: string, content: string) {
        this.title = title;
        this.content = content;
        this.collaborators = [];
    }
}