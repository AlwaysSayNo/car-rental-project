const submitForm = (form, event, filterSelect, sortSelect) => {
    const none = 'none'

    const map = new Map();
    map.set('optgroupLabel', '');
    map.set('optionValue', '');


    const filterBy = addHiddenInForm(form, 'filterBy', '');
    const filterValue = addHiddenInForm(form, 'filterValue', '');
    if(filterSelect.value !== none){
        getFromOptgroup(filterSelect, map);
        
        filterBy.value = map.get('optgroupLabel');
        filterValue.value = map.get('optionValue');
    } else{
        filterBy.disabled = true;
        filterValue.disabled = true;
    }

    const sortBy = addHiddenInForm(form, 'sortBy', '');
    const direction = addHiddenInForm(form, 'direction', '');
    if(sortSelect.value !== none){
        getFromOptgroup(sortSelect, map);

        sortBy.value = map.get('optgroupLabel');
        direction.value = map.get('optionValue');
    } else{
        sortBy.disabled = true;
        direction.disabled = true;
    }

}

const getFromOptgroup = (select, map) => {
    const sortChildren = Array.prototype.slice.call(select.children);

    sortChildren.forEach(function (optgroup){
        if(optgroup.tagName.toLowerCase() === 'optgroup'){
            const options = Array.prototype.slice.call(optgroup.children);

            options.forEach(function (option){
                if(option.selected){
                    map.set('optgroupLabel', optgroup.label);
                    map.set('optionValue', option.value);
                }
            })
        }
    })
}

const addHiddenInForm = (form, name, value) => {
    const newChild = document.createElement('input')
    newChild.setAttribute("type", "hidden");
    newChild.setAttribute("name", name);
    newChild.setAttribute("value", value);
    form.appendChild(newChild);
    return newChild;
}