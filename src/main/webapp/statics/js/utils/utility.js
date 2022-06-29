// get next element with specific class
export function findPlaceholder(className, subject) {
    let next = subject.nextElementSibling;
    let placeholder;

    while (next) {
        if (next.classList.contains(className)) {
            placeholder = next;
            break;
        }

        next = next.nextElementSibling;
    }

    return placeholder
}
