const addHiddenInForm = (form, name, value) => {
    const newChild = document.createElement('input')
    newChild.setAttribute("type", "hidden");
    newChild.setAttribute("name", name);
    newChild.setAttribute("value", value);
    form.appendChild(newChild);
    return newChild;
}