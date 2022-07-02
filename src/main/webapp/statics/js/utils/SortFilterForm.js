submitForm = (form, event, filterSelect, sortSelect) => {
    const none = 'none'

    const map = new Map();
    map.set('optgroupLabel', '');
    map.set('optionValue', '');


    const filterBy = form.querySelector('#filterBy');
    const filterValue = form.querySelector('#filterValue');
    if(filterSelect.value !== none){
        getFromOptgroup(filterSelect, map);
        filterBy.value = map.get('optgroupLabel');
        filterValue.value = map.get('optionValue');
    } else{
        filterBy.disabled = true;
        filterValue.disabled = true;
    }

    const sortBy = form.querySelector('#sortBy');
    const direction = form.querySelector('#direction');
    if(sortSelect.value !== none){
        getFromOptgroup(sortSelect, map);

        sortBy.value = map.get('optgroupLabel');
        direction.value = map.get('optionValue');
    } else{
        sortBy.disabled = true;
        direction.disabled = true;
    }

}

getFromOptgroup = (select, map) => {
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