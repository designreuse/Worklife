import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {PostDto} from "../../shared/posts/post-dto.model";
import {PostService} from "../../shared/posts/post.service";
import {BsModalService, ModalOptions} from 'ngx-bootstrap';
import {FileUploadModalComponent} from '../../file-upload-modal/file-upload-modal.component';
import {isNull} from 'util';

const options: ModalOptions = {
  class: 'modal-sm',
  backdrop: 'static',
};

@Component({
  selector: 'app-post-form',
  templateUrl: './post-form.component.html',
  styleUrls: ['./post-form.component.css']
})
export class PostFormComponent implements OnInit {
  postForm: FormGroup;

  constructor(private _fb: FormBuilder,
              private postService: PostService,
              private _modal: BsModalService) {
  }

  ngOnInit() {
    this.postForm = this._fb.group({
      context: [null, Validators.required],
      visible: [1, Validators.required],
      file: [null]
    });
  }

  openUploadFileModal() {

    const submit = (fileInfo) => {

      this.postForm.get('file').setValue(fileInfo);

      return new Promise(function (resolve, reject) {
        setTimeout(function () {
          resolve('ok');
        }, 0);
      });
    };

    const initialState = {
      submitButton: 'Add',
      title: 'Add file to the post',
      submit: submit
    };

    this._modal.show(FileUploadModalComponent, {...options, initialState});
  };

  onSubmit(postForm: FormGroup) {
    if (postForm.valid) {

      const file = postForm.get('file').value;
      let postRequest = new PostDto() as PostDto;
      postRequest.load(this.postForm);

      if (!isNull(file)) {

        let formData = new FormData();
        Object.keys(postRequest).forEach(key => {
          formData.append(key, postRequest[key]);
        });

        formData.append('file', file, file.name);

        this.postService.addPost(formData)
          .then(() => {
          });
        return;
      }
      this.postService.addPost(postRequest)
        .then(() => {
        });
    }
  }
}