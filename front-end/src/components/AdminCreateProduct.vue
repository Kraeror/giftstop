<template>
    <div class="max-w-4xl mx-auto py-10 px-4">
        <h1 class="text-3xl font-bold text-center text-gray-800 mb-6">Създай нов продукт</h1>

        <form @submit.prevent="submitProduct" @keydown.enter.prevent class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 space-y-6">
            <div>
                <label class="block text-gray-700 text-sm font-bold mb-2" for="title">Заглавие</label>
                <input v-model="product.title" id="title" type="text" placeholder="Заглавие"
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
            </div>

            <div>
                <label class="block text-gray-700 text-sm font-bold mb-2" for="description">Описание</label>
                <quill-editor
                    v-model:value="product.description"
                    contentType="html"
                    theme="snow"
                    class="bg-white rounded shadow border"
                    style="min-height: 200px"
                />
            </div>

            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="price">Цена (лв)</label>
                    <input v-model.number="product.price" id="price" type="number" min="0" step="0.01"
                        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
                </div>

                <div>
                    <label class="block text-gray-700 text-sm font-bold mb-2" for="salePrice">Промо цена (лв)</label>
                    <input v-model.number="product.salePrice" id="salePrice" type="number" min="0" step="0.01"
                        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
                </div>
            </div>

            <div>
                <label class="block text-gray-700 text-sm font-bold mb-2" for="slug">Slug (линк)</label>
                <input v-model="product.slug" id="slug" type="text" placeholder="my-product"
                    class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-pink" />
            </div>

            <div>
                <label class="block text-gray-700 text-sm font-bold mb-2">Избери категория</label>
                <CategoryPicker
                    :categories="allCategories"
                    @select="onCategorySelected"
                    @unselect="onCategoryRemoved"
                />
            </div>

            <div class="mb-2">
                <label class="block text-gray-700 text-sm font-bold mb-2">Снимки</label>
                <input ref="imageInput" accept="image/*" type="file" multiple @change="handleImageUpload" class="hidden" />

                <button @click.prevent="$refs.imageInput.click()" type="button"
                    class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600 focus:outline-none focus:ring">
                    📷 Качи снимки
                </button>

                <div class="flex gap-2 mt-2 flex-wrap justify-center">
                    <div v-for="(img, index) in product.images" :key="index" class="relative">
                        <img :src="img.preview" class="w-[100px] h-[100px] object-cover rounded border" />
                        <button @click.prevent="removeImage(index)"
                            class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs">✕</button>
                    </div>
                </div>

                <div v-if="product.images.length" class="text-xs text-gray-600 mt-1 text-center">
                    {{ product.images.length }} {{ product.images.length === 1 ? 'снимка' : 'снимки' }} избрани
                </div>
            </div>

            <!-- Добавени допълнителни полета -->
            <div class="space-y-4">
                <h2 class="text-lg font-semibold text-gray-800">Допълнителни полета към продукта</h2>

                <div v-for="(field, index) in product.customFields" :key="index" class="border p-4 rounded shadow space-y-2">
                    <div class="flex gap-4 items-center">
                        <input v-model="field.name" placeholder="Име на полето" class="flex-1 border rounded px-3 py-1" />
                        <select v-model="field.type" @change="onFieldTypeChange(field)" class="border rounded px-2 py-1">
                            <option disabled value="">Избери тип</option>
                            <option v-for="type in additionalFieldTypes" :key="type" :value="type">{{ type }}</option>
                        </select>
                        <button @click.prevent="product.customFields.splice(index, 1)" class="text-red-500">✕</button>
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
                            <div v-for="(color, i) in field.items" :key="i" class="flex flex-col items-center border p-2 rounded">
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
                                >✕</button>
                            </div>
                        </div>
                        <button
                            @click.prevent="field.items.push({ name: '', value: '#000000' })"
                            class="text-sm text-blue-600 hover:underline mt-2"
                        >+ Добави цвят</button>
                    </div>


                    <div v-else-if="field.type === 'Снимка'" class="text-gray-500 italic">
                        Това поле ще позволи на клиента да прикачи снимка.
                    </div>

                    <div v-else-if="field.type === 'Падащо меню'" class="space-y-2">
                        <div v-for="(item, i) in field.items" :key="i" class="flex gap-2 items-center">
                            <input v-model="field.items[i]" placeholder="Опция" class="flex-1 border px-3 py-1 rounded" />
                            <button @click.prevent="field.items.splice(i, 1)" class="text-red-500">✕</button>
                        </div>
                        <button @click.prevent="field.items.push('')" class="text-sm text-blue-600 hover:underline">+ Нова опция</button>
                    </div>
                </div>

                <button @click.prevent="addCustomFieldToProduct" class="text-[#F777AC] hover:underline text-sm">+ Добави поле</button>
            </div>

             <div class="space-y-4">
                <h2 class="text-lg font-semibold text-gray-800">Варианти</h2>
                <div v-for="(variant, index) in product.variants" :key="index" class="space-y-4 border p-4 rounded shadow-md">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Име на вариант</label>
                        <input v-model="variant.title" placeholder="Име на вариант"
                            class="shadow border rounded py-2 px-3 text-gray-700 w-full focus:outline-none focus:ring-2 focus:ring-pink" />
                    </div>

                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Цена (лв)</label>
                        <input v-model.number="variant.price" placeholder="Цена" type="number" min="0" step="0.01"
                            class="shadow border rounded py-2 px-3 text-gray-700 w-full focus:outline-none focus:ring-2 focus:ring-pink" />
                    </div>

                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Промо цена (лв)</label>
                        <input v-model.number="variant.salePrice" placeholder="Промо цена" type="number" min="0" step="0.01"
                            class="shadow border rounded py-2 px-3 text-gray-700 w-full focus:outline-none focus:ring-2 focus:ring-pink" />
                    </div>

                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Снимка на варианта</label>
                        <input
                            type="file"
                            accept="image/*"
                            :ref="'variantImageInput_' + index"
                            @change="handleVariantImageUpload($event, index)"
                            class="hidden"
                        />

                        <button
                            @click.prevent="$refs['variantImageInput_' + index][0].click()"
                            type="button"
                            class="inline-flex items-center gap-2 px-4 py-2 bg-[#F777AC] text-white text-sm font-semibold rounded hover:bg-pink-600 focus:outline-none focus:ring"
                        >
                            📷 Качи снимка
                        </button>

                        <div v-if="variant.imagePreview" class="mt-2 relative w-[100px] h-[100px] mx-auto">
                            <img :src="variant.imagePreview" class="w-full h-full object-cover rounded border" />
                            <button
                                @click.prevent="removeVariantImage(index)"
                                class="absolute top-0 right-0 bg-red-500 text-white rounded-full p-1 text-xs"
                            >
                                ✕
                            </button>
                        </div>
                    </div>
                    <button @click.prevent="removeVariant(index)" class="text-red-500 hover:underline text-sm mt-2">Премахни варианта</button>
                </div>

                <button @click.prevent="addVariant" class="text-[#F777AC] hover:underline text-sm">+ Добави вариант</button>
            </div>

            <!-- Submit бутон -->
            <div class="pt-6">
                <button type="submit"
                    class="bg-[#F777AC] hover:bg-pink-600 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline w-full">
                    Запази продукта
                </button>
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
                price: 0,
                salePrice: 0,
                slug: '',
                active: true,
                categoryIds: [],
                subcategoryIds: [],
                images: [],
                variants: [],
                customFields: []
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
        addCustomFieldToProduct() {
            this.product.customFields.push({
                name: '',
                type: '',
                items: [],
                preview: null,
                file: null
            });
        },
        onFieldTypeChange(field) {
            field.items = [];
            field.preview = null;
            field.file = null;
            field.value = '';

            if (field.type === 'Текст (свободен)') {
                field.name = 'Текст';
            } else if (field.type === 'Числово поле') {
                field.name = 'Число';
            } else if (field.type === 'Име') {
                field.name = 'Име';
            } else if (field.type === 'Снимка') {
                field.name = 'Прикачи снимка';
            } else if (field.type === 'Цвят') {
                field.name = 'Цвят';
                field.items = [{ name: '', value: '#000000' }];
            } else if (field.type === 'Дата') {
                field.name = 'Дата';
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
                const formData = new FormData();

                formData.append('product', new Blob([JSON.stringify(this.product)], { type: 'application/json' }));

                for (const image of this.product.images) {
                    if (image.file) {
                        formData.append('images', image.file);
                    }
                }

                this.product.variants.forEach((variant) => {
                    const variantData = { ...variant };
                    delete variantData.imageFile;
                    delete variantData.imagePreview;

                    formData.append('variants', new Blob([JSON.stringify(variantData)], { type: 'application/json' }));

                    if (variant.imageFile) {
                        formData.append('variantImages', variant.imageFile);
                    }
                });

                for (const field of this.product.customFields) {
                    const fieldCopy = { ...field };
                    delete fieldCopy.preview;
                    delete fieldCopy.file;

                    formData.append('customFields', new Blob([JSON.stringify(fieldCopy)], { type: 'application/json' }));

                    if (field.type === 'image' && field.file) {
                        formData.append('customFieldImages', field.file);
                    }
                }

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
            imagePreview: null
        });
        },
        removeVariant(index) {
            this.product.variants.splice(index, 1);
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
