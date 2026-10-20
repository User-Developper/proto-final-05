document.addEventListener("DOMContentLoaded", () => {

    const input = document.getElementById("reportImages");
    const preview = document.getElementById("imagePreview");
    const count = document.getElementById("imageCount");

    if (!input || !preview || !count) {
        return;
    }

    let selectedFiles = [];

    input.addEventListener("change", () => {

        const newFiles = Array.from(input.files);

        newFiles.forEach(file => {

            if (!file.type.startsWith("image/")) {
                return;
            }

            const alreadyExists = selectedFiles.some(
                existingFile =>
                    existingFile.name === file.name &&
                    existingFile.size === file.size &&
                    existingFile.lastModified === file.lastModified
            );

            if (!alreadyExists) {
                selectedFiles.push(file);
            }
        });

        updateInputFiles();
        renderPreview();
    });


    function renderPreview() {

        preview.innerHTML = "";

        selectedFiles.forEach((file, index) => {

            const item = document.createElement("div");
            item.className = "image-preview-item";

            const image = document.createElement("img");

            const removeButton = document.createElement("button");

            removeButton.type = "button";
            removeButton.className = "image-remove";

            removeButton.setAttribute(
                "aria-label",
                "Supprimer cette photo"
            );

            removeButton.innerHTML =
                '<i class="fa-solid fa-xmark"></i>';

            const objectUrl = URL.createObjectURL(file);

            image.src = objectUrl;
            image.alt = "Photo sélectionnée";

            image.onload = () => {
                URL.revokeObjectURL(objectUrl);
            };


            removeButton.addEventListener("click", () => {

                selectedFiles.splice(index, 1);

                updateInputFiles();
                renderPreview();
            });


            item.appendChild(image);
            item.appendChild(removeButton);

            preview.appendChild(item);
        });

        updateCount();
    }


    function updateCount() {

        if (selectedFiles.length === 0) {

            count.textContent = "";

            return;
        }


        if (selectedFiles.length === 1) {

            count.textContent =
                "1 photo sélectionnée";

        } else {

            count.textContent =
                `${selectedFiles.length} photos sélectionnées`;
        }
    }


    function updateInputFiles() {

        const dataTransfer = new DataTransfer();

        selectedFiles.forEach(file => {

            dataTransfer.items.add(file);

        });

        input.files = dataTransfer.files;
    }

});