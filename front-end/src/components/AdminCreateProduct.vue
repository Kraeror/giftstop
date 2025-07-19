<template>
  <div class="max-w-4xl mx-auto py-10 px-4">
    <h1 class="text-3xl font-bold text-center text-gray-800 mb-6">Създай нов продукт</h1>

    <form @submit.prevent="submitProduct" @keydown.enter.prevent class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 space-y-6">
      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="title">Заглавие на продукта</label>
        <input v-model="product.title" id="title" type="text" placeholder="Магнитче със снимка"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="description">Описание</label>
        <quill-editor v-model:value="product.description" contentType="html" theme="snow"
          class="bg-white rounded shadow border" style="min-height: 200px" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="slug">Slug (линк)</label>
        <input v-model="product.slug" id="slug" type="text" placeholder="Magnitche-sas-snimka"
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2">Избери категория</label>
        <CategoryPicker :categories="allCategories" @select="onCategorySelected" @unselect="onCategoryRemoved" />
      </div>

      <div>
        <label class="block text-gray-700 text-sm font-bold mb-2">Снимки на продукта</label>
        <input type="file" accept="image/*" multiple @change="handleImageUpload" ref="imageInput" class="hidden" />
        <button @click.prevent="$refs.imageInput.click()" class="inline-flex items-center gap-2 px-4 py-2 bg-pink-500 text-white rounded hover:bg-pink-600">
            📷 Качи снимки
        </button>

        <div class="flex flex-wrap gap-4 mt-4">
            <div v-for="(image, index) in product.images" :key="index" class="relative w-[100px] h-[100px]">
            <img :src="image.preview" class="w-full h-full object-cover rounded border" />
            <button @click.prevent="removeImage(index)" class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
            </div>
        </div>
    </div>
      <div class="space-y-6">
        <h2 class="text-lg font-semibold text-gray-800">Варианти</h2>
        <div v-for="(variant, index) in product.variants" :key="index"
          class="space-y-4 border p-4 rounded shadow-md bg-gray-50">
            <div v-if="!variant.defaultVariant">
                <label class="block text-sm font-medium text-gray-700 mb-1">Снимка на варианта</label>
                <input type="file" accept="image/*" :ref="'variantImageInput_' + index" @change="handleVariantImageUpload($event, index)" class="hidden" />
                <button @click.prevent="openVariantImageInput(index)" type="button"
                    class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600">
                    📷 Качи снимка
                </button>
                <div v-if="variant.imagePreview" class="mt-2 relative w-[100px] h-[100px] mx-auto">
                    <img :src="variant.imagePreview" class="w-full h-full object-cover rounded border" />
                    <button @click.prevent="removeVariantImage(index)"
                    class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
                </div>
            </div>
          <div v-if="!variant.defaultVariant">
            <label class="block text-sm font-medium text-gray-700 mb-1">Име на варианта</label>
            <input v-model="variant.title" placeholder="Магнитче с черна мачка" class="shadow border rounded py-2 px-3 text-gray-700 w-full" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Цена (лв)</label>
            <input v-model.number="variant.price" type="number" min="0" step="0.01"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Промо цена (лв)</label>
            <input v-model.number="variant.salePrice" type="number" min="0" step="0.01"
              class="shadow border rounded py-2 px-3 text-gray-700 w-full" />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              <input type="radio" name="defaultVariant" :value="index" :checked="variant.defaultVariant" @change="setDefaultVariant(index)" />
              Основен вариант
            </label>
          </div>

          <div class="space-y-2">
            <h3 class="text-sm font-semibold text-gray-700">Допълнителни полета</h3>

            <div
                v-for="(field, index) in variant.customFields"
                :key="index"
                class="border p-4 rounded shadow space-y-2"
            >
                <div class="flex gap-4 items-center">
                <input
                    v-model="field.name"
                    placeholder="Име на полето"
                    class="flex-1 border rounded px-3 py-1"
                />
                <select
                    v-model="field.type"
                    @change="onFieldTypeChange(field)"
                    class="border rounded px-2 py-1"
                >
                    <option disabled value="">Избери тип</option>
                    <option
                    v-for="type in additionalFieldTypes"
                    :key="type"
                    :value="type"
                    >
                    {{ type }}
                    </option>
                </select>
                <button
                    @click.prevent="variant.customFields.splice(index, 1)"
                    class="text-red-500"
                >
                    ✕
                </button>
                </div>

                <div v-if="field.type === 'Текст (свободен)'">
                <div class="text-gray-500 italic">Клиентът ще въведе свободен текст.</div>
                </div>

                <div v-else-if="field.type === 'Име'">
                <div class="text-gray-500 italic">Клиентът трябва да въведе само букви.</div>
                </div>

                <div v-else-if="field.type === 'Числово поле'">
                <div class="text-gray-500 italic">Клиентът ще въведе само числа.</div>
                </div>

                <div v-else-if="field.type === 'Дата'" class="text-gray-500 italic">
                Това поле ще позволи на клиента да избере дата.
                </div>

                <div v-else-if="field.type === 'Цвят'" class="space-y-2">
                <div class="flex flex-wrap gap-2">
                    <div
                    v-for="(color, i) in field.items"
                    :key="i"
                    class="flex flex-col items-center border p-2 rounded"
                    >
                    <input
                        type="color"
                        v-model="color.value"
                        class="w-[50px] h-[30px] rounded"
                    />
                    <input
                        v-model="color.name"
                        placeholder="Име на цвят"
                        class="mt-1 text-xs border px-2 py-1 rounded w-[90px]"
                    />
                    <button
                        @click.prevent="field.items.splice(i, 1)"
                        class="text-red-500 text-xs mt-1"
                    >
                        ✕
                    </button>
                    </div>
                </div>
                <button
                    @click.prevent="field.items.push({ name: '', value: '#000000' })"
                    class="text-sm text-blue-600 hover:underline mt-2"
                >
                    + Добави цвят
                </button>
                </div>

                <div v-else-if="field.type === 'Снимка'" class="text-gray-500 italic">
                Това поле ще позволи на клиента да прикачи снимка.
                </div>

                <div v-else-if="field.type === 'Падащо меню'" class="space-y-2">
                <div
                    v-for="(item, i) in field.items"
                    :key="i"
                    class="flex gap-2 items-center"
                >
                    <input
                    v-model="field.items[i]"
                    placeholder="Опция"
                    class="flex-1 border px-3 py-1 rounded"
                    />
                    <button
                    @click.prevent="field.items.splice(i, 1)"
                    class="text-red-500"
                    >
                    ✕
                    </button>
                </div>
                <button
                    @click.prevent="field.items.push('')"
                    class="text-sm text-blue-600 hover:underline"
                >
                    + Нова опция
                </button>
                </div>
            </div>

            <button
                @click.prevent="variant.customFields.push({ name: '', type: '', items: [] })"
                class="text-sm text-blue-600 hover:underline"
            >
                + Добави поле
            </button>
            </div>

          <button v-if="product.variants.length > 1" @click.prevent="removeVariant(index)"
            class="text-red-500 hover:underline text-sm">Премахни варианта</button>
        </div>

        <button @click.prevent="addVariant" class="text-[#F777AC] hover:underline text-sm">+ Добави вариант</button>
      </div>

      <div class="pt-6">
        <button type="submit"
          class="bg-[#F777AC] hover:bg-pink-600 text-white font-bold py-2 px-4 rounded w-full">Запази продукта</button>
      </div>
    </form>
  </div>
</template>

<script>
import { quillEditor } from 'vue3-quill';
import 'quill/dist/quill.snow.css';
import axios from 'axios';
import CategoryPicker from '@/components/CategoryPicker.vue';

export default {
    name: 'AdminCreateProduct',
    components: {
        QuillEditor: quillEditor,
        CategoryPicker
    },
    data() {
        return {
            additionalFieldTypes: [
                'Текст (свободен)',
                'Числово поле',
                'Име',
                'Снимка',
                'Цвят',
                'Падащо меню',
                'Дата'
            ],
            product: {
                title: '',
                description: '',
                slug: '',
                categoryIds: [],
                subcategoryIds: [],
                images: [],
                variants: [
                    {
                        title: '',
                        price: 0,
                        salePrice: 0,
                        active: true,
                        imageFile: null,
                        imagePreview: null,
                        defaultVariant: true,
                        customFields: []
                    }
                ]
            },
            autoSlug: true,
            allCategories: [],
        };
    },
    watch: {
        'product.title'(newTitle) {
            if (this.autoSlug) {
                this.product.slug = this.generateSlug(newTitle);
            }
        },
    },
    mounted() {
        axios.get('/api/admin/categories').then(res => {
            this.allCategories = res.data;
        }).catch(e => {
            console.error('Грешка при зареждане на категории', e);
        });
    },
    methods: {
        onFieldTypeChange(field) {
            field.items = [];
            field.preview = null;
            field.file = null;
            field.value = '';

            switch (field.type) {
                case 'Текст (свободен)': field.name = 'Текст'; break;
                case 'Числово поле': field.name = 'Число'; break;
                case 'Име': field.name = 'Име'; break;
                case 'Снимка': field.name = 'Прикачи снимка'; break;
                case 'Цвят':
                    field.name = 'Цвят';
                    field.items = [{ name: '', value: '#000000' }];
                    break;
                case 'Падащо меню':
                    field.name = 'Избери опция';
                    field.items = [''];
                    break;
                case 'Дата':
                    field.name = 'Дата';
                    break;
            }
        },
        onCustomFieldImageChange(event, field) {
            const file = event.target.files[0];
            if (!file) return;
            field.file = file;
            field.preview = URL.createObjectURL(file);
            field.value = file.name;
        },
        generateSlug(title) {
            const map = {
                а: 'a', б: 'b', в: 'v', г: 'g', д: 'd', е: 'e', ж: 'zh', з: 'z',
                и: 'i', й: 'y', к: 'k', л: 'l', м: 'm', н: 'n', о: 'o', п: 'p',
                р: 'r', с: 's', т: 't', у: 'u', ф: 'f', х: 'h', ц: 'ts', ч: 'ch',
                ш: 'sh', щ: 'sht', ъ: 'a', ь: '', ю: 'yu', я: 'ya',
            };

            return title
                .toLowerCase()
                .replace(/[а-яё]/g, c => map[c] || '')
                .replace(/[^a-z0-9]+/g, '-')
                .replace(/^-+|-+$/g, '')
                .replace(/--+/g, '-');
        },
        handleImageUpload(event) {
            const newFiles = Array.from(event.target.files);
            const newImageObjects = newFiles.map(file => ({
                file,
                preview: URL.createObjectURL(file),
            }));
            this.product.images.push(...newImageObjects);
            event.target.value = '';
        },
        removeImage(index) {
            this.product.images.splice(index, 1);
            if (this.product.images.length === 0 && this.$refs.imageInput) {
                this.$refs.imageInput.value = '';
            }
        },
        async submitProduct() {
        try {
            // Валидация: поне един вариант и един трябва да е default
            if (!this.product.variants.length) {
                alert("Моля, добавете поне един вариант.");
                return;
            }

            if (!this.product.variants.some(v => v.defaultVariant)) {
                alert("Моля, маркирайте един от вариантите като основен (default).");
                return;
            }

            const formData = new FormData();

            // Продуктът (без изображенията) се изпраща като JSON blob
            const productData = {
                title: this.product.title,
                description: this.product.description,
                slug: this.product.slug,
                categoryIds: this.product.categoryIds,
                imageFileNames: [], // ще се попълни на бекенда
                variants: this.product.variants.map(variant => ({
                    title: variant.title,
                    price: variant.price,
                    salePrice: variant.salePrice,
                    active: variant.active,
                    imageFileName: '', // ще се попълни на бекенда
                    defaultVariant: variant.defaultVariant
                })),
            };

            formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));

            // Основни изображения
            for (const image of this.product.images) {
                if (image.file) {
                    formData.append('images', image.file);
                }
            }

            // Варианти (всеки поотделно + евентуално изображение)
            this.product.variants.forEach(variant => {
                const variantData = {
                    title: variant.title,
                    price: variant.price,
                    salePrice: variant.salePrice,
                    active: variant.active,
                    imageFileName: '',
                    defaultVariant: variant.defaultVariant,
                    customFields: variant.customFields || []
                };

                formData.append('variants', new Blob([JSON.stringify(variantData)], { type: 'application/json' }));

                if (variant.imageFile && !variant.defaultVariant) {
                    formData.append('variantImages', variant.imageFile);
                }

                // снимки за custom полета от типа „Снимка“
                for (const field of variant.customFields) {
                    if (field.type === 'Снимка' && field.file) {
                        formData.append('customFieldImages', field.file);
                    }
                }
            });

            const response = await axios.post('/api/admin/products', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
            });

            console.log('Успешно:', response.data);
            alert('Продуктът е създаден успешно!');
            } catch (error) {
                console.error('Грешка при запис:', error);
                alert('Възникна грешка при запис на продукта.');
            }
        },
        addVariant() {
            this.product.variants.push({
                title: '',
                price: 0,
                salePrice: 0,
                active: true,
                imageFile: null,
                imagePreview: null,
                defaultVariant: this.product.variants.length === 0,
                customFields: []
            });
        },
        setDefaultVariant(index) {
            this.product.variants.forEach((v, i) => {
                if (i === index) {
                v.defaultVariant = true;
                // премахни снимката ако има
                v.imageFile = null;
                v.imagePreview = null;
                } else {
                v.defaultVariant = false;
                }
            });
        },
        removeVariant(index) {
            if (this.product.variants.length === 1) {
                alert('Не може да изтриете последния вариант.');
                return;
            }

            const wasDefault = this.product.variants[index].defaultVariant;

            this.product.variants.splice(index, 1);

            if (wasDefault && this.product.variants.length > 0) {
                this.product.variants[0].defaultVariant = true;
            }
        },
        handleVariantImageUpload(event, index) {
            const file = event.target.files[0];
            if (!file) return;
            const reader = new FileReader();
            reader.onload = () => {
                this.product.variants[index].imageFile = file;
                this.product.variants[index].imagePreview = reader.result;
            };
            reader.readAsDataURL(file);
        },
        removeVariantImage(index) {
            this.product.variants[index].imageFile = null;
            this.product.variants[index].imagePreview = null;
        },
        openVariantImageInput(index) {
            const input = this.$refs['variantImageInput_' + index];
            if (input && input[0]) {
                input[0].click();
            }
        },
        onCategorySelected(category) {
            this.selectedCategory = category;
            this.product.categoryIds = [category.id];
        },
    },
};
</script>

<style scoped>
body {
    background-color: #f9fafb;
}

:focus {
    --tw-ring-color: #F777AC !important;
}
</style>
