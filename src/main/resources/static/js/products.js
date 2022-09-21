var myModal;
var imageModal;
var id;
var imageLink;
var selectedRow;
const table = document.getElementById("product-list");
const imageModalContent = document.getElementById('imageModalContent');

//Document Ready
document.addEventListener("DOMContentLoaded", () => {
    priceMask();
    myModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
    imageModal = new bootstrap.Modal(document.getElementById('imageModal'));
});

//Pega o id do item ao clicar no botão delete
table.onclick = ({target}) =>{
    if(target.classList.contains("delete")){
        configureDeleteConfirmationModal(target);
    }else if(target.parentElement.classList.contains("delete")){
        configureDeleteConfirmationModal(target.parentElement)
    }else if(target.classList.contains("image")){
        configureImageModal(target);
    }else if(target.parentElement.classList.contains("image")){
        configureImageModal(target.parentElement);
    }
};
document.querySelector(".modal-confirm-deletion").addEventListener("click", (e) => {
    deleteItem(id);
});

//MASCARA PARA PREÇO
function priceMask(){
    new Cleave('#price', {
        numeral: true,
        numeralDecimalMark: ',',
        delimiter: '.'
    });
}

function configureImageModal(target){
    selectedRow = target.parentElement.parentElement;
    imageLink = selectedRow.children[5].textContent;
    imageModalContent.innerHTML = `<img style="display: block; max-width: 100%; height: auto;" src="/api/products/image/${imageLink}" class="img-responsive">`;
    imageModal.show();
}

//Show allerts
function showAlert(message, className){
    const div = document.createElement("div");
    div.className = `alert alert-${className}`;

    div.appendChild(document.createTextNode(message));
    const container = document.querySelector(".container");
    const main = document.querySelector(".main");
    container.insertBefore(div, main);
    div.focus();

    setTimeout(() => document.querySelector(".alert").remove(), 3000);
}

//Configura o alerta nos botões delete
function prepareConfirmationModal(title, message){
    document.getElementById("modal-title").innerHTML = title;
    document.getElementById("modal-message").innerHTML = message;
}
function configureDeleteConfirmationModal(target){
    selectedRow = target.parentElement.parentElement;
    id = selectedRow.children[0].textContent;
    prepareConfirmationModal("Confirme a operação", "Você deseja realmente excluir esse registro?");
    myModal.show();
}
function deleteItem(id){
    var url = "/admin/product/excluir/" + id; 
    fetch(url)
        .then(res => {
            if(!res) {
                return res.text().then(text => { throw new Error(text) })
            }
            else {
                return res.json();
            }    
        })
        .then((data) => {
            showAlert("Item deletado!", "success");
            selectedRow.remove();
        }).catch((error) =>{
            showAlert("Ops.. não foi possivel deletar o registro!", "danger")
            console.log(error);
        });
}