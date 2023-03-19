# checkout-microservice-template
Template to create a barebones microservice (with options) for a checkout microservice

- The basic Spring Boot Application framework as well as the project framework has been laid out in this template.
- The template currently has placeholder values marked with `template-replace`, along with the value
   - ex: `[template-replace:servicename]` would translate to `mobile-orchestrator` if the mobile-orchestrator was being built
- This template also has different connection configs for REST and SOAP based communications *(These classes are not vital to each service)*
   - Properties will need to be passed into configuration classes

## Adding secrets to pull from pipeline:
ESOA Credentials and other secrets must be removed from the codebase for security measures. These secrets will be saved into the Managed Pipeline and passed into the project during the Pipeline/Deployment.

Process of getting secrets into a deployment through managed pipeline:
- The Secrets will need to be uploaded to the Managed Pipeline
- During the Continuous Deployment (CD) phase of the Pipeline, the Pipeline will run the Deployment Script defined in the project. The Deployment Script should be configured to grab the matching Pipeline-Credential-ID and have the script run.
- The script will run commands to create secrets in the Kubernetes cluster/pod that is being created/deployed
- The Kubernetes Pod will then look at the deployment definition and match the created Kubernetes Secret to environment variables in the Kubernetes Pod
- The environment variables of the Kubernetes pod will then be picked up by the running Java Application (similar to how the `ENV` variable is picked up to define runtime environments)


### `managedPipelineProperties.yaml` setup:
Environment variables for the **managed pipeline** are used to define values for secret names used in the **Deployment Scripts**.

In this case of deploying ESOA secrets, a Managed Pipeline Environment Variable of `SECRET_NAME` is defined. This will be used to run the command to create and name the Kubernetes Cluster Secret.

### Deployment Script setup:
To upload and use the Secrets into the cluster from the pipeline, the Deployment Scripts must be configured:
- The Deployment Script should hook into the Managed Pipeline Credentials/Secrets and grab the ID associated with the secret being used.

  >- In **Jenkins**, the `withCredentials()` hook is used to grab from the Jenkins Credentials keychain.
  >```
  >withCredentials([string(credentialsId: 'JENKINS_CREDENTIAL_ID', variable: 'SECRET_VARIABLE')]){
  >  // Deployment Script ...
  >}
  >```
- The `SECRET_NAME` from [earlier](https://github.com/AAInternal/checkout-microservice-template#managedpipelinepropertiesyaml-setup) is used when creating the Kubernetes Cluster Secret that will be ued by the Pod. The deployment script should include the following to log into the cluster, delete the already-existing secret (to make sure the secret is updated per-deployment), and create the secret in the cluster. (Multiple literals/keys can be defined per secret)
```
ibmcloud ks cluster config --cluster "${cluster}"
kubectl delete secret ${SECRET_NAME} -n ${NAMESPACE} --ignore-not-found=true
kubectl create secret -n ${NAMESPACE} generic ${SECRET_NAME} --from-literal={insert.first.key.here}=$SECRET_VARIABLE --from-literal={insert.second.key.here}=$SECRET_VARIABLE
```

### Deployment Definition Setup:
Once the Kubernete Secrets are created, the Deployment will need to know which Secret to use.
- In the `k8/deployment-definition.yaml.template` file, container environment properties will need to be setup
- The definition will create an environment variable (`CONTAINER_ENVIRONMENT_SECRET_NAME` in the example code) by looking at the Kubernetes Cluster secret (`KUBERNETES_SECRET_NAME` in the example code) that was created [earlier](https://github.com/AAInternal/checkout-microservice-template#deployment-script-setup)
- The value from the matching key from the Kubernetes Secret (`KEY.DEFINED.IN.KUBERNETES.SECRET` in example code) will be used as the container environment variable
```
env:
  ...
  - name: [CONTAINER_ENVIRONMENT_SECRET_NAME]
    valueFrom:
      secretKeyRef:
          name: [KUBERNETES_SECRET_NAME]
          key: [KEY.DEFINED.IN.KUBERNETES.SECRET]
```

## Adding Specifically ESOA Credentials to Jenkins Pipelines
Perform the following steps to add certs to Jenkins. These added certs will be used
by the respective `deploy.groovy` scripts (dev, stage, prod).
```
withCredentials([string(credentialsId: 'ESOA_KEYSTORE_DEV_AND_STAGE', variable: 'KEYSTORE'),
string(credentialsId: 'ESOA_PASSWORD_DEV_AND_STAGE', variable: 'PASSWORD')])
```

**Step 1**

Command to generate encoded text from JKS file:
```
base64 [JKS_FILENAME] | awk 'NF {sub(/\r/, ""); printf "%s",$0;}' > [OUTPUT_TXT_FILENAME]
```

**Step 2**

Under the "Credential" menu of the repo's CD pipeline in Jenkins, add two credentials of type secret text with these IDs:
- `ESOA_KEYSTORE_DEV_AND_STAGE`: The value for this will be the content of `[OUTPUT_TXT_FILENAME]` file created in *Step 1*
- `ESOA_PASSWORD_DEV_AND_STAGE`: The value for this will be the password of the original JKS file used in *Step 1*
Repeat the above steps for Prod cert as well.

**Step 3**

Ensure that the secrets are grabbed and put into matching environment variables where needed as described in [Adding secrets to pull from pipeline](https://github.com/AAInternal/checkout-microservice-template#adding-secrets-to-pull-from-pipeline)

### Setting Up the Credentials to run `[template-replace:servicename]` Locally:
To set up the service to run locally, the SSL Certificates will need to entered as environment variables of the application's Run Configuration.

__Environment Variables:__
- `ESOA_KEYSTORE`: Certificate Binary
- `ESOA_PASSWORD`: JKS Certificate Password

## Clean up to be done if there is no need of ESOA Credentials:
- Delete the SECRET_NAME env variable in `managedPipelineProperties.yaml` for each deployment
- Delete `ESOA_KEYSTORE` and `ESOA_PASSWORD` env variables from `deployment-definition.yaml.template` file
- Delete the `deployWithSecret()` function in `deploy-qa.groovy`, `deploy-both-regions-stage.groovy` and `deploy-both-regions-prod.groovy` files. Use the `deploy()` function instead.
- Delete `HTTPS keystore details ESOA CS` section from `application.properties` and `application-local.properties` files